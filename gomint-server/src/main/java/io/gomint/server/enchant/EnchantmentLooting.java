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
@RegisterInfo( id = 14 )
public class EnchantmentLooting extends Enchantment implements io.gomint.enchant.EnchantmentLooting {

    /**
     * Create new enchantment smite
     *
     * @param level of this enchantment
     */
    public EnchantmentLooting( short level ) {
        super( (short) 3, level );
    }

}
