/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.crafting;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.util.PacketDataOutputStream;
import io.gomint.taglib.NBTTagCompound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
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
	public void serialize( PacketBuffer buffer, PacketDataOutputStream intermediate ) throws IOException {
		// For whatever reason there are two versions of smelting recipes only with flipped
		// order of metadata and item ID; I don't really see when which type of recipe will
		// be used or whether or even is any difference between the two types thus I go with
		// type 2 as 3 is only used for one single recipe as far as I have observed up to now:
		final int type = 2;

		buffer.writeInt( type );

		byte[]         raw   = null;
		NBTTagCompound extra = this.outcome.getNbtData();
		if ( extra != null ) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			extra.writeTo( bout, false, ByteOrder.LITTLE_ENDIAN );
			raw = bout.toByteArray();
			bout.close();
		}
		int expectedLength = 11 + ( raw == null ? 0 : raw.length );
		buffer.writeInt( expectedLength );

		if ( type == 2 ) {
			buffer.writeShort( this.input.getData() );
			buffer.writeShort( (short) this.input.getId() );
		} else {
			buffer.writeShort( (short) this.input.getId() );
			buffer.writeShort( this.input.getData() );
		}

		buffer.writeShort( (short) this.outcome.getId() );
		buffer.writeByte( (byte) this.outcome.getAmount() );
		buffer.writeShort( this.outcome.getData() );
		buffer.writeShort( (short) ( raw == null ? 0 : raw.length ) );
		if ( raw != null ) {
			buffer.writeBytes( raw );
		}
	}

}
