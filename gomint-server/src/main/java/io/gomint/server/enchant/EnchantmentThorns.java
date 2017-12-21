/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.enchant;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 5 )
public class EnchantmentThorns extends Enchantment implements io.gomint.enchant.EnchantmentThorns {

    /**
     * Create new enchantment smite
     *
     * @param level of this enchantment
     */
    public EnchantmentThorns( short level ) {
        super( (short) 3, level );
    }

}
