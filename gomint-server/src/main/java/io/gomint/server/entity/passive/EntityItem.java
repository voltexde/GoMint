package io.gomint.server.entity.passive;

import io.gomint.entity.passive.EntityItemDrop;
import io.gomint.inventory.item.ItemStack;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.EntityType;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketAddItemEntity;
import io.gomint.server.util.Values;
import io.gomint.server.world.WorldAdapter;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@ToString
public class EntityItem extends Entity implements EntityItemDrop {

    private final ItemStack itemStack;
    @Getter
    private long pickupTime;
    private boolean isReset;

    private float lastUpdateDt;

    /**
     * Construct a new Entity
     *
     * @param itemStack The itemstack which should be dropped
     * @param world     The world in which this entity is in
     */
    public EntityItem( ItemStack itemStack, WorldAdapter world ) {
        super( EntityType.ITEM_DROP, world );
        this.itemStack = itemStack;
        this.setSize( 0.25f, 0.25f );
        setPickupDelay( 2, TimeUnit.SECONDS );
    }

    @Override
    public <T extends ItemStack> T getItemStack() {
        return (T) ( (io.gomint.server.inventory.item.ItemStack) this.itemStack ).clone();
    }

    @Override
    public void setPickupDelay( long duration, TimeUnit timeUnit ) {
        this.pickupTime = System.currentTimeMillis() + timeUnit.toMillis( duration );
    }

    @Override
    public void update( long currentTimeMS, float dT ) {
        // Entity base tick (movement)
        super.update( currentTimeMS, dT );

        this.lastUpdateDt += dT;
        if ( this.lastUpdateDt >= Values.CLIENT_TICK_RATE ) {
            if ( this.onGround && !this.isReset ) {
                this.setVelocity( new Vector( 0, 0, 0 ) ); // Reset velocity
                this.isReset = true;
            }

            this.lastUpdateDt = 0;
        }
    }

    @Override
    protected void fall() {

    }

    @Override
    public Packet createSpawnPacket() {
        PacketAddItemEntity packetAddItemEntity = new PacketAddItemEntity();
        packetAddItemEntity.setEntityId( this.getEntityId() );
        packetAddItemEntity.setItemStack( this.itemStack );
        packetAddItemEntity.setX( this.getPositionX() );
        packetAddItemEntity.setY( this.getPositionY() );
        packetAddItemEntity.setZ( this.getPositionZ() );
        packetAddItemEntity.setMotionX( this.getMotionX() );
        packetAddItemEntity.setMotionY( this.getMotionY() );
        packetAddItemEntity.setMotionZ( this.getMotionZ() );
        return packetAddItemEntity;
    }

}
