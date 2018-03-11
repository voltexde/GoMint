package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.entity.Entity;
import io.gomint.server.inventory.item.ItemBucket;
import io.gomint.server.world.PlacementData;
import io.gomint.server.world.UpdateReason;
import io.gomint.world.block.BlockLiquid;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.Facing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class Liquid extends Block implements BlockLiquid {

    private static final Logger LOGGER = LoggerFactory.getLogger( Liquid.class );

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
        if ( !isFlowing() ) {
            return -1;
        }

        if ( updateReason == UpdateReason.BLOCK_ADDED ) {
            if ( isUpdateScheduled() ) {
                return -1;
            }

            return currentTimeMS + getTickDiff(); // Water updates every 5 client ticks
        }

        // LOGGER.info( "Data {} @ {}", this.getBlockData(), this.location );

        if ( this.getBlockData() == 8 ) {
            // spread( 0 );
        }

        if ( this.getBlockData() <= 7 ) {
            if ( this.getBlockData() < 7 ) {
                // spread( this.getBlockData() + 1 );
            }

            // Check if source block is there
            // checkForSource( this.getBlockData() == 0 ? 8 : this.getBlockData() - 1 );
        }

        return currentTimeMS + getTickDiff(); // Water updates every 5 client ticks
    }

    private void checkForSource( int neededData ) {
        boolean found = false;

        for ( Facing facing : Facing.values() ) {
            Block other = (Block) this.getSide( facing );
            if ( other.getType() == this.getType() ) {
                if ( other.getBlockData() == neededData ) {
                    found = true;
                    break;
                }
            }
        }

        if ( !found ) {
            this.setType( Air.class );
        }
    }

    private void spread( int data ) {
        // Spread in all directions if possible
        for ( Facing facing : Facing.values() ) {
            Block other = (Block) this.getSide( facing );
            if ( other.getType() == BlockType.AIR ) {
                other.setBlockFromPlacementData( new PlacementData( getBlockId(), (byte) data, null ) );
            }
        }
    }


}
