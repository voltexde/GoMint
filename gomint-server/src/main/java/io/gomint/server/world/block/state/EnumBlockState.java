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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnumBlockState<E extends Enum<E>> extends BlockState<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger( EnumBlockState.class );

    private final E[] enumValues;

    public EnumBlockState( Block block, E[] values ) {
        super( block );
        this.enumValues = values;
    }

    public EnumBlockState( Block block, E[] values, Predicate<List<BlockState>> predicate ) {
        super( block, predicate );
        this.enumValues = values;
    }

    public EnumBlockState( Block block, E[] values, Predicate<List<BlockState>> predicate, int shift ) {
        super( block, predicate, shift );
        this.enumValues = values;
    }

    @Override
    protected int cap() {
        return this.enumValues.length - 1;
    }

    @Override
    public void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition ) {
        this.setState( this.enumValues[placedItem.getData()] );
    }

    @Override
    protected void data( short data ) {
        if ( data >= this.enumValues.length ) {
            this.setState( this.enumValues[0] );
            LOGGER.error( "Incorrect block data value in block", new Exception() );
            return;
        }

        this.setState( this.enumValues[data] );
    }

    @Override
    protected short data() {
        return (short) this.getState().ordinal();
    }

}
