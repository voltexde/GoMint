/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface BlockBed extends Block {

    /**
     * Get the other half of this multi block structure,
     *
     * @return the other half of the bed or null when no other half has been found
     */
    BlockBed getOtherHalf();

    /**
     * Is this block the head part of this multi block structure?
     *
     * @return true if this is the head part, false if not
     */
    boolean isHeadPart();

}
