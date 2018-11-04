/*
 * Copyright (c) 2018 Gomint team
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
public class BlockfaceFromPlayerBlockState extends BlockfaceBlockState {

    public BlockfaceFromPlayerBlockState( Block block ) {
        super( block );
    }

    public BlockfaceFromPlayerBlockState( Block block, boolean detectUpDown ) {
        super( block, detectUpDown );
    }

    public BlockfaceFromPlayerBlockState( Block block, Predicate<List<BlockState>> predicate ) {
        super( block, predicate );
    }

    public BlockfaceFromPlayerBlockState( Block block, Predicate<List<BlockState>> predicate, int shift ) {
        super( block, predicate, shift );
    }

    @Override
    public void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition ) {
        if ( !this.detectUpDown && ( face == BlockFace.UP || face == BlockFace.DOWN ) ) {
            face = BlockFace.NORTH;
        }

        this.setState( face );
    }

}
