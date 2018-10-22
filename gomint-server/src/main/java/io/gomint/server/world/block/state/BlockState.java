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
import lombok.Getter;

import java.util.List;
import java.util.function.Predicate;

/**
 * @param <T> type of the state
 * @author geNAZt
 * @version 1.0
 */
public abstract class BlockState<T> {

    @Getter
    private T state;
    private final int shift;
    private final Block block;

    public BlockState( Block block ) {
        this( block, blockStates -> true ); // Always convert
    }

    public BlockState( Block block, Predicate<List<BlockState>> predicate ) {
        this( block, predicate, 0 );
    }

    public BlockState( Block block, Predicate<List<BlockState>> predicate, int shift ) {
        // Remember to store the block
        this.block = block;

        // Remember how much to shift
        this.shift = shift;

        // Register this to the block
        block.registerState( this, predicate );
    }

    public void setState( T state ) {
        this.state = state;

        if ( this.block.ready() ) {
            this.block.updateBlock();
        }
    }

    /**
     * Get the data cap this block state accepts
     *
     * @return cap of this block state
     */
    protected abstract int cap();

    /**
     * Detect from a player
     *
     * @param player        from which we generate data
     * @param placedItem    which has been used to get this block
     * @param face          which the client has clicked on
     * @param block         which should be replaced
     * @param clickedBlock  which has been clicked by the client
     * @param clickPosition where the client clicked on the block
     */
    public abstract void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition );

    /**
     * Tell the block state its data from which it should decide what state to be in
     *
     * @param data from the block which decides which state to choose
     */
    protected abstract void data( short data );

    /**
     * What data value should be stored for this block state
     *
     * @return the data value which should be stored for this state
     */
    protected abstract short data();

    /**
     * Get the data for the block
     *
     * @return byte data for the block
     */
    public short toData() {
        short internalData = this.data();
        if ( this.shift > 0 ) {
            return (short) ( internalData << this.shift );
        }

        return internalData;
    }

    /**
     * Get state from block data
     *
     * @param data from the block
     */
    public void fromData( short data ) {
        if ( this.shift > 0 ) {
            this.data( (short) ( ( data >> this.shift ) & this.cap() ) );
            return;
        }

        this.data( (short) ( data & this.cap() ) );
    }

}
