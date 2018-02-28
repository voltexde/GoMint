package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.entity.Entity;
import io.gomint.server.world.UpdateReason;
import io.gomint.world.block.BlockLiquid;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class Liquid extends Block implements BlockLiquid {

    @Override
    public float getFillHeight() {
        short data = this.getBlockData();
        if ( data > 8 ) {
            data = 8;
        }

        return ( ( data + 1 ) / 9f );
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public void stepOn( Entity entity ) {
        // Reset fall distance
        entity.resetFallDistance();
    }

    @Override
    public boolean canBeReplaced( ItemStack item ) {
        return true;
    }

    public abstract int getTickDiff();
    public abstract boolean isFlowing();

    @Override
    public long update( UpdateReason updateReason, long currentTimeMS, float dT ) {

        return currentTimeMS + getTickDiff(); // Water updates every 5 client ticks
    }
}
