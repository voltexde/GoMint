/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world;

/**
 * @author geNAZt
 * @author BlackyPaw
 * @version 1.0
 */
public interface Block {

	/**
     * Gets the ID of the block.
     *
     * @return The ID of the block
     */
    int getBlockId();

	/**
     * Gets the block's data value (0 - 15)
     *
     * @return The block's data value
     */
    byte getBlockData();

	/**
     * Sets the block's ID. Any values that lie outside of the valid ID range will be
     * set to 0 (Air).
     *
     * @param id The ID to set
     */
    void setBlockId( int id );

	/**
     * Sets the block's data value. Any values that lie outside of the valid ID range (0 - 15) will be
     * set to 0 (no data).
     *
     * @param data The data value ofto set
     */
    void setBlockData( byte data );

}
