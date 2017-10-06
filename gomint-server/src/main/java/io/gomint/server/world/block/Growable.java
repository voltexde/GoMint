package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.BlockPosition;
import io.gomint.math.Location;
import io.gomint.server.util.random.FastRandom;
import io.gomint.server.world.UpdateReason;

/**
 * @author geNAZt
 * @version 1.0
 * <p>
 * This is a generic class for anything that can grow with metadata like crops, beetroot etc.
 */
public abstract class Growable extends Block {

    @Override
    public boolean beforePlacement( ItemStack item, Location location ) {
        // Check if we place on farmland
        return location.getWorld().getBlockAt( location.toBlockPosition().add( BlockPosition.DOWN ) ) instanceof Farmland;
    }

    @Override
    public long update( UpdateReason updateReason, long currentTimeMS, float dT ) {
        if ( updateReason == UpdateReason.NEIGHBOUR_UPDATE ) {
            // Check if farmland is still under us
            if ( !( this.world.getBlockAt( this.location.toBlockPosition().add( BlockPosition.DOWN ) ) instanceof Farmland ) ) {
                this.world.breakBlock( this.location.toBlockPosition() );
            }
        } else if ( updateReason == UpdateReason.RANDOM ) {
            // Check for growth state
            if ( this.getBlockData() < 0x07 ) {
                int random = FastRandom.current().nextInt( 3 );

                // Grow it
                if ( random == 1 ) { // 33.3% chance of growing
                    // TODO: Some sort of growth event

                    this.setBlockData( (byte) ( this.getBlockData() + 1 ) );
                    this.updateBlock();
                }
            }
        }

        return -1;
    }

}
