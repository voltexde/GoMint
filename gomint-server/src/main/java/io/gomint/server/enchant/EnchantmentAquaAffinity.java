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
@RegisterInfo( id = 8 )
public class EnchantmentAquaAffinity extends Enchantment implements io.gomint.enchant.EnchantmentAquaAffinity {

    /**
     * Create new enchantment smite
     *
     * @param level of this enchantment
     */
    public EnchantmentAquaAffinity( short level ) {
        super( (short) 1, level );
    }

    @Override
    public byte getMinEnchantAbility( short level ) {
        return 1;
    }

    @Override
    public byte getMaxEnchantAbility( short level ) {
        return 41;
    }

}
