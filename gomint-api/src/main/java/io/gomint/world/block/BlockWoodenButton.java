/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world.block;

import io.gomint.world.block.data.WoodType;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface BlockWoodenButton extends Block {

    /**
     * Get the state of this button
     *
     * @return true when currently pressed, false otherwise
     */
    boolean isPressed();

    /**
     * Press the button (it will release after 1 second)
     */
    void press();

    /**
     * Get the attached facing which has been used to attach this button to a block
     *
     * @return attached face
     */
    BlockFace getAttachedFace();

    /**
     * Set the attached facing of this button
     *
     * @param face which is attached to a block
     */
    void setAttachedFace( BlockFace face );

    /**
     * Get the type of wood from which this button has been made
     *
     * @return type of wood
     */
    WoodType getWoodType();

    /**
     * Set the type of wood for this button
     *
     * @param woodType type of wood
     */
    void setWoodType( WoodType woodType );

}
