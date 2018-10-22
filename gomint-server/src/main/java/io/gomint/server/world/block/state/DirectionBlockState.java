/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.block.state;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.Vector;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.world.block.Block;
import io.gomint.world.block.BlockFace;
import io.gomint.world.block.data.Direction;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author geNAZt
 * @version 1.0
 */
public class DirectionBlockState extends BlockState<Direction> {

    public DirectionBlockState( Block block ) {
        super( block );
    }

    public DirectionBlockState( Block block, Predicate<List<BlockState>> predicate ) {
        super( block, predicate );
    }

    public DirectionBlockState( Block block, Predicate<List<BlockState>> predicate, int shift ) {
        super( block, predicate, shift );
    }

    @Override
    protected int cap() {
        return 3;
    }

    @Override
    public void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition ) {
        switch ( face ) {
            case UP:
            case DOWN:
                this.setState( Direction.UP_DOWN );
                break;

            case NORTH:
            case SOUTH:
                this.setState( Direction.NORTH_SOUTH );
                break;

            default:
                this.setState( Direction.EAST_WEST );
                break;
        }
    }

    @Override
    protected void data( short data ) {
        this.setState( Direction.values()[data] );
    }

    @Override
    protected short data() {
        return (short) this.getState().ordinal();
    }
}
