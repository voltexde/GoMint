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
@RegisterInfo( id = 11 )
public class EnchantmentBaneOfArthopods extends Enchantment implements io.gomint.enchant.EnchantmentBaneOfArthopods {

    /**
     * Create new enchantment smite
     *
     * @param level of this enchantment
     */
    public EnchantmentBaneOfArthopods( short level ) {
        super( (short) 5, level );
    }

    @Override
    public byte getMinEnchantAbility( short level ) {
        return (byte) ( 5 + ( level - 1 ) * 8 );
    }

    @Override
    public byte getMaxEnchantAbility( short level ) {
        return (byte) ( getMinEnchantAbility( level ) + 20 );
    }

}
