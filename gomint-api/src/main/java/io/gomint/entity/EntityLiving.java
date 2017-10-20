/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.entity;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface EntityLiving extends Entity {

    /**
     * Set entity health
     *
     * @param amount of health this entity has
     */
    void setHealth( float amount );

    /**
     * Get the amount of health this entity has
     *
     * @return health of entity
     */
    float getHealth();

    /**
     * Get the maximum amount of health this entity can have
     *
     * @return maximum amount of health this entity can have
     */
    float getMaxHealth();

}
