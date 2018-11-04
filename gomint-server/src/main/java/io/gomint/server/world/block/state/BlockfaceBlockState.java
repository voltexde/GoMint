/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.block.state;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.Vector;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.util.Bearing;
import io.gomint.server.util.Things;
import io.gomint.server.world.block.Block;
import io.gomint.world.block.BlockFace;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockfaceBlockState extends BlockState<BlockFace> {

    private final boolean detectUpDown;

    public BlockfaceBlockState( Block block ) {
        super( block );
        this.detectUpDown = false;
    }

    public BlockfaceBlockState( Block block, boolean detectUpDown ) {
        super( block );
        this.detectUpDown = detectUpDown;
    }

    public BlockfaceBlockState( Block block, Predicate<List<BlockState>> predicate ) {
        super( block, predicate );
        this.detectUpDown = false;
    }

    public BlockfaceBlockState( Block block, Predicate<List<BlockState>> predicate, int shift ) {
        super( block, predicate, shift );
        this.detectUpDown = false;
    }

    @Override
    protected int cap() {
        return 5;
    }

    @Override
    public void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition ) {
        this.setState( face );
    }

    @Override
    protected void data( short data ) {
        this.setState( Things.convertFromDataToBlockFace( data ) );
    }

    @Override
    protected short data() {
        switch ( this.getState() ) {
            case DOWN:
                return 0;
            case UP:
                return 1;
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
            default:
                return 0;
        }
    }

}
