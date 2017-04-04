/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world;

import io.gomint.world.block.Block;

/**
 * @author geNAZt
 * @author BlackyPaw
 * @version 1.0
 */
public interface Chunk {

    /**
     * Gets the block at the specified position.
     *
     * @param x The x-coordinate of the block
     * @param y The y-coordinate of the block
     * @param z The z-coordinate of the block
     * @return The block itself or null if the given coordinates lie not within this chunk
     */
    <T extends Block> T getBlockAt( int x, int y, int z );

}
