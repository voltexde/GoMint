package io.gomint.server.entity;

import io.gomint.entity.DamageCause;
import io.gomint.server.entity.component.AIBehaviourComponent;
import io.gomint.server.entity.pathfinding.PathfindingEngine;
import io.gomint.server.inventory.InventoryHolder;
import io.gomint.server.world.WorldAdapter;

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
public abstract class EntityLiving extends Entity implements InventoryHolder {

    // AI of the entity:
    protected AIBehaviourComponent behaviour;
    // Pathfinding engine of the entity:
    protected PathfindingEngine pathfinding;

    protected Map<String, AttributeInstance> attributes = new HashMap<>();

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

    protected void addAttribute( Attribute attribute ) {
        AttributeInstance instance = attribute.create();
        this.attributes.put( instance.getKey(), instance );
    }

    public void setAttribute( Attribute attribute, float value ) {
        AttributeInstance instance = this.attributes.get( attribute.getKey() );
        if ( instance != null ) {
            instance.setValue( value );
        }
    }

    @Override
    protected void fall() {
        double damage = Math.floor( this.fallDistance - 3 );
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
        super.update( currentTimeMS, dT );
        this.behaviour.update( currentTimeMS, dT );
    }

}
