/*
 * Copyright (c) 2016, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.server.registry.SkipRegister;

/**
 * @author geNAZt
 * @version 1.0
 */
@SkipRegister
public enum AttributeModifier {

    // Damage
    ITEM_ATTACK_DAMAGE,
    STRENGTH_EFFECT,

    // Movement
    SPEED_EFFECT,
    SLOWNESS_EFFECT,
    SPRINT_MULTIPLY

}
