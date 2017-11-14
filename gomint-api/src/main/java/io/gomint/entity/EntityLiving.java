/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.entity;

import io.gomint.event.entity.EntityDamageEvent;

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

    /**
     * Set this entity immobile
     *
     * @param value true if immobile, false if not
     */
    void setImmobile( boolean value );

    /**
     * Get the entities last damage source
     *
     * @return damage soruce or null when not damaged
     */
    EntityDamageEvent.DamageSource getLastDamageSource();

    /**
     * Get the entity which dealt the last damage
     *
     * @return null when {@link #getLastDamageSource()} is not {@link io.gomint.event.entity.EntityDamageEvent.DamageSource#ENTITY_ATTACK}
     * or {@link io.gomint.event.entity.EntityDamageEvent.DamageSource#PROJECTILE} or the entity has already been despawned
     */
    Entity getLastDamageEntity();

}
