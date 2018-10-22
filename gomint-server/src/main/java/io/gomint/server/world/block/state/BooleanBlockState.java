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
import io.gomint.server.world.block.Block;
import io.gomint.world.block.BlockFace;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BooleanBlockState extends BlockState<Boolean> {

    public BooleanBlockState( Block block ) {
        super( block );
    }

    public BooleanBlockState( Block block, Predicate<List<BlockState>> predicate ) {
        super( block, predicate );
    }

    public BooleanBlockState( Block block, Predicate<List<BlockState>> predicate, int shift ) {
        super( block, predicate, shift );
    }

    @Override
    protected int cap() {
        return 1;
    }

    @Override
    public void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition ) {
        this.setState( false );
    }

    @Override
    protected short data() {
        return (byte) ( this.getState() ? 1 : 0 );
    }

    @Override
    protected void data( short data ) {
        this.setState( data == 1 );
    }

}
