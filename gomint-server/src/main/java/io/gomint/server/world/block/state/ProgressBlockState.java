/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.block.state;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.MathUtils;
import io.gomint.math.Vector;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.world.block.Block;
import io.gomint.world.block.BlockFace;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ProgressBlockState extends BlockState<Float> {

    private Consumer<Void> maxedProgressConsumer;
    private int max;
    @Getter
    private float step;

    public ProgressBlockState( Block block, int max, Consumer<Void> maxedProgressConsumer ) {
        super( block );
        this.step = 1f / max;
        this.maxedProgressConsumer = maxedProgressConsumer;
        this.max = max;
    }

    public boolean progress() {
        this.setState( this.getState() + this.step );
        if ( 1f - this.getState() <= MathUtils.EPSILON ) {
            this.maxedProgressConsumer.accept( null );
            return false;
        }

        return true;
    }

    @Override
    protected int cap() {
        return this.max;
    }

    @Override
    public void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition ) {
        this.setState( 0f );
    }

    @Override
    protected void data( short data ) {
        this.setState( data * this.step );
    }

    @Override
    protected short data() {
        return (short) Math.round( this.getState() * this.max );
    }


}
