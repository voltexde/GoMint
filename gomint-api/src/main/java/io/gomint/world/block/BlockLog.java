/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world.block;

import io.gomint.world.block.data.Direction;
import io.gomint.world.block.data.WoodType;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface BlockLog extends Block {

    /**
     * Is this log stripped
     *
     * @return true when stripped, false when not
     */
    boolean isStripped();

    /**
     * Set stripped status of this log
     *
     * @param stripped true when the log should be stripped, false if not
     */
    void setStripped( boolean stripped );

    /**
     * Set the type of log
     *
     * @param type of log
     */
    void setWoodType( WoodType type );

    /**
     * Get the type of this log
     *
     * @return type of log
     */
    WoodType getWoodType();

    /**
     * Set the direction of the log
     *
     * @param direction of the log
     */
    void setLogDirection( Direction direction );

    /**
     * Get the direction of this log
     *
     * @return direction of the log
     */
    Direction getLogDirection();

}
