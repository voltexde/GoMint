/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world.block;

import io.gomint.world.block.data.BlockColor;
import io.gomint.world.block.data.Facing;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface BlockGlazedTerracotta extends Block {

    /**
     * Set the color of this block
     *
     * @param color of this block
     */
    void setColor( BlockColor color );

    /**
     * Get the color of this block
     *
     * @return color of this block
     */
    BlockColor getColor();

    /**
     * Set the blocks facing
     *
     * @param facing of this block
     */
    void setFacing( Facing facing );

    /**
     * Get the facing of this block
     *
     * @return facing of this block
     */
    Facing getFacing();

}
