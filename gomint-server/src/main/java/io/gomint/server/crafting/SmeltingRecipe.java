/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.crafting;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.util.EnumConnectors;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Resembles a smelting recipe which may be used in conjunction with furnaces.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class SmeltingRecipe extends Recipe {

    private ItemStack input;
    private ItemStack outcome;

    public SmeltingRecipe( ItemStack input, ItemStack outcome, UUID uuid ) {
        super( uuid );
        this.input = input;
        this.outcome = outcome;
    }

    @Override
    public Collection<ItemStack> getIngredients() {
        return Collections.singleton( this.input );
    }

    @Override
    public Collection<ItemStack> createResult() {
        return Collections.singletonList( this.outcome.clone() );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        // The type of this recipe is defined after the input metadata
        buffer.writeSignedVarInt( this.input.getData() == 0 ? 2 : 3 );

        // We need to custom write items
        buffer.writeSignedVarInt( EnumConnectors.MATERIAL_CONNECTOR.convert( this.input.getMaterial() ).getOldId() );
        if ( this.input.getData() != 0 ) buffer.writeSignedVarInt( this.input.getData() );

        Packet.writeItemStack( this.outcome, buffer, false );
    }

}
