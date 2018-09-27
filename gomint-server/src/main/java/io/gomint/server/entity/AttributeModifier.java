/*
 * Copyright (c) 2016, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
@Getter
public enum AttributeModifier {

    // Damage
    ITEM_ATTACK_DAMAGE( "item_damage" ),
    STRENGTH_EFFECT( "strength" ),
    WEAKNESS_EFFECT( "weakness" ),

    // Movement
    SPEED_EFFECT( "speed" ),
    SLOWNESS_EFFECT( "slowness" ),
    SPRINT( "sprint" ),

    // Vanilla
    RANDOM_SPAWN_BONUS( "RandomSpawnBonus" );

    private final String name;

}
