package io.gomint.server.entity;

import com.koloboke.collect.ObjCursor;
import io.gomint.entity.DamageCause;
import io.gomint.event.entity.EntityDamageEvent;
import io.gomint.event.entity.EntityHealEvent;
import io.gomint.math.Vector;
import io.gomint.server.entity.component.AIBehaviourComponent;
import io.gomint.server.entity.metadata.MetadataContainer;
import io.gomint.server.entity.pathfinding.PathfindingEngine;
import io.gomint.server.inventory.InventoryHolder;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketEntityEvent;
import io.gomint.server.network.packet.PacketRespawnPosition;
import io.gomint.server.network.packet.PacketSpawnEntity;
import io.gomint.server.util.Values;
import io.gomint.server.util.collection.EntityHashSet;
import io.gomint.server.world.WorldAdapter;
import io.gomint.util.Numbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Common base class for all entities that live. All living entities possess
 * an AI which is the significant characteristic that marks an entity as being
 * alive in GoMint's definition.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class EntityLiving extends Entity implements InventoryHolder, io.gomint.entity.EntityLiving {

    private static final Logger LOGGER = LoggerFactory.getLogger( EntityLiving.class );

    // AI of the entity:
    protected AIBehaviourComponent behaviour;
    // Pathfinding engine of the entity:
    protected PathfindingEngine pathfinding;

    protected Map<String, AttributeInstance> attributes = new HashMap<>();

    private float lastUpdateDT = 0;
    private EntityHashSet attachedEntities = EntityHashSet.withExpectedSize( 10 );

    /**
     * Constructs a new EntityLiving
     *
     * @param type  The type of the Entity
     * @param world The world in which this entity is in
     */
    protected EntityLiving( EntityType type, WorldAdapter world ) {
        super( type, world );
        this.behaviour = new AIBehaviourComponent();
        this.pathfinding = new PathfindingEngine( this.getTransform() );
        this.initAttributes();
    }

    private void initAttributes() {
        addAttribute( Attribute.ABSORPTION );
        addAttribute( Attribute.ATTACK_DAMAGE );
        addAttribute( Attribute.FOLLOW_RANGE );
        addAttribute( Attribute.HEALTH );
        addAttribute( Attribute.MOVEMENT_SPEED );
        addAttribute( Attribute.KNOCKBACK_RESISTANCE );
    }

    protected float addAttribute( Attribute attribute ) {
        AttributeInstance instance = attribute.create();
        this.attributes.put( instance.getKey(), instance );
        return instance.getValue();
    }

    public float getAttribute( Attribute attribute ) {
        AttributeInstance instance = this.attributes.get( attribute.getKey() );
        if ( instance != null ) {
            return instance.getValue();
        }

        return addAttribute( attribute );
    }

    public AttributeInstance getAttributeInstance( Attribute attribute ) {
        return this.attributes.get( attribute.getKey() );
    }

    public void setAttribute( Attribute attribute, float value ) {
        AttributeInstance instance = this.attributes.get( attribute.getKey() );
        if ( instance != null ) {
            instance.setValue( value );
        }
    }

    @Override
    protected void fall() {
        double damage = this.fallDistance - 3;
        if ( damage > 0 ) {
            this.attack( damage, DamageCause.FALL );
        }
    }

    public void attack( double damage, DamageCause cause ) {
        // TODO: Implement damage handling
    }

    // ==================================== UPDATING ==================================== //

    @Override
    public void update( long currentTimeMS, float dT ) {
        if ( this.isDead() || this.getHealth() <= 0 ) {
            return;
        }

        super.update( currentTimeMS, dT );
        this.behaviour.update( currentTimeMS, dT );

        // Check for client tick stuff
        this.lastUpdateDT += dT;
        if ( this.lastUpdateDT >= Values.CLIENT_TICK_RATE ) {
            // Check for block stuff
            this.metadataContainer.setDataFlag( MetadataContainer.DATA_INDEX, EntityFlag.BREATHING, !this.isInsideLiquid() );

            this.lastUpdateDT = 0;
        }
    }

    @Override
    public void setHealth( float amount ) {
        AttributeInstance attributeInstance = this.attributes.get( Attribute.HEALTH.getKey() );
        attributeInstance.setValue( Math.max( attributeInstance.getMinValue(), amount ) );
    }

    @Override
    public float getHealth() {
        return this.getAttribute( Attribute.HEALTH );
    }

    /**
     * Construct a spawn packet for this entity
     *
     * @return the spawn packet of this entity, ready to be sent to the client
     */
    public Packet createSpawnPacket() {
        // Broadcast spawn entity packet:
        PacketSpawnEntity packet = new PacketSpawnEntity();
        packet.setEntityId( this.id );
        packet.setEntityType( this.type );
        packet.setX( this.getPositionX() );
        packet.setY( this.getPositionY() );
        packet.setZ( this.getPositionZ() );
        packet.setVelocityX( this.getMotionX() );
        packet.setVelocityY( this.getMotionY() );
        packet.setVelocityZ( this.getMotionZ() );
        packet.setPitch( this.getPitch() );
        packet.setYaw( this.getYaw() );
        packet.setAttributes( this.attributes.values() );
        packet.setMetadata( this.getMetadata() );
        return packet;
    }

    @Override
    public float getMaxHealth() {
        return this.getAttributeInstance( Attribute.HEALTH ).getMaxValue();
    }

    /**
     * Heal this entity by given amount and cause
     *
     * @param amount of heal
     * @param cause  of this heal
     */
    public void heal( float amount, EntityHealEvent.Cause cause ) {
        EntityHealEvent event = new EntityHealEvent( this, amount, cause );
        this.world.getServer().getPluginManager().callEvent( event );

        if ( !event.isCancelled() ) {
            this.setHealth( this.getHealth() + amount );
        }
    }

    @Override
    public boolean damage( EntityDamageEvent damageEvent ) {
        // Call event
        if ( !super.damage( damageEvent ) ) {
            return false;
        }

        // Armor calculations
        float damage = applyArmorReduction( damageEvent );
        float health = Numbers.fastCeil( this.getHealth() - damage );

        LOGGER.debug( "Remaining health: " + health + " / " + this.getHealth() );

        // Set health
        this.setHealth( health <= 0 ? 0 : health );

        // Send animation
        PacketEntityEvent entityEvent = new PacketEntityEvent();
        entityEvent.setEntityId( this.id );
        entityEvent.setEventId( ( health <= 0 ) ? EntityEvent.DEATH.getId() : EntityEvent.HURT.getId() );

        ObjCursor<io.gomint.entity.Entity> attachedEntities = this.attachedEntities.cursor();
        while ( attachedEntities.moveNext() ) {
            EntityPlayer entityPlayer = (EntityPlayer) attachedEntities.elem();
            entityPlayer.getConnection().addToSendQueue( entityEvent );
        }

        if ( this instanceof EntityPlayer ) {
            ( (EntityPlayer) this ).getConnection().addToSendQueue( entityEvent );

            if ( health <= 0 ) {
                this.kill();
            }
        }

        return true;
    }

    protected abstract void kill();

    float applyArmorReduction( EntityDamageEvent damageEvent ) {
        return damageEvent.getDamage();
    }

    @Override
    public void attach( EntityPlayer player ) {
        this.attachedEntities.add( player );
    }

    @Override
    public void detach( EntityPlayer player ) {
        this.attachedEntities.remove( player );
    }

    void resetAttributes() {
        for ( AttributeInstance instance : this.attributes.values() ) {
            instance.reset();
        }
    }

}
