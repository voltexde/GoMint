/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.crafting.Recipe;
import io.gomint.server.crafting.ShapedRecipe;
import io.gomint.server.crafting.ShapelessRecipe;
import io.gomint.server.crafting.SmeltingRecipe;
import io.gomint.server.network.Protocol;
import io.gomint.server.util.PacketDataOutputStream;
import io.gomint.taglib.NBTTagCompound;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author BlackyPaw
 * @version 1.0
 */
@Data
@EqualsAndHashCode( callSuper = false )
public class PacketCraftingRecipes extends Packet {

	private Collection<Recipe> recipes;

	public PacketCraftingRecipes() {
		super( Protocol.PACKET_CRAFTING_RECIPES );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		PacketDataOutputStream pout = new PacketDataOutputStream();
		buffer.writeInt( this.recipes.size() );
		try {
			for ( Recipe recipe : this.recipes ) {
				recipe.serialize( buffer, pout );
				pout.reset();
			}
			pout.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		buffer.writeByte( (byte) 1 ); // Unknown use
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		int count = buffer.readInt();
		this.recipes = new HashSet<>( count );
		for ( int i = 0; i < count; ++i ) {
			int type   = buffer.readInt();
			int length = buffer.readInt();

			switch ( type ) {
				case 0:
					// Shapeless Recipe:
					this.recipes.add( this.readShapelessRecipe( buffer ) );
					break;

				case 1:
					// Shaped Recipe:
					this.recipes.add( this.readShapedRecipe( buffer ) );
					break;

				case 2:
					// Smelting Recipe:
					this.recipes.add( this.readSmeltingRecipe2( buffer ) );
					break;

				case 3:
					// Smelting Recipe:
					this.recipes.add( this.readSmeltingRecipe3( buffer ) );
					break;

				default:
					// Whatever:
					buffer.skip( length );
					break;
			}
		}
		buffer.skip( 1 ); // Unknown use
	}

	private ShapelessRecipe readShapelessRecipe( PacketBuffer buffer ) {
		int count = buffer.readInt();

		ItemStack[] ingredients = new ItemStack[count];
		for ( int i = 0; i < count; ++i ) {
			ingredients[i] = this.readItemStack( buffer );
		}

		count = buffer.readInt();
		ItemStack[] outcome = new ItemStack[count];
		for ( int i = 0; i < count; ++i ) {
			outcome[i] = this.readItemStack( buffer );
		}
		UUID uuid = buffer.readUUID();

		return new ShapelessRecipe( ingredients, outcome, uuid );
	}

	private ShapedRecipe readShapedRecipe( PacketBuffer buffer ) {
		int width  = buffer.readInt();
		int height = buffer.readInt();

		ItemStack[] arrangement = new ItemStack[width * height];
		for ( int i = 0; i < width * height; ++i ) {
			arrangement[i] = this.readItemStack( buffer );
		}

		int         count   = buffer.readInt();
		ItemStack[] outcome = new ItemStack[count];
		for ( int i = 0; i < count; ++i ) {
			outcome[i] = this.readItemStack( buffer );
		}
		UUID uuid = buffer.readUUID();

		return new ShapedRecipe( width, height, arrangement, outcome, uuid );
	}

	private SmeltingRecipe readSmeltingRecipe2( PacketBuffer buffer ) {
		// Read metadata first:
		short metadata = buffer.readShort();
		short id       = buffer.readShort();
		return this.readSmeltingRecipeBase( buffer, id, metadata );
	}

	private SmeltingRecipe readSmeltingRecipe3( PacketBuffer buffer ) {
		// Read ID first:
		short id       = buffer.readShort();
		short metadata = buffer.readShort();
		return this.readSmeltingRecipeBase( buffer, id, metadata );
	}

	private SmeltingRecipe readSmeltingRecipeBase( PacketBuffer buffer, short id, short metadata ) {
		ItemStack input   = new ItemStack( id, metadata, 1 );
		ItemStack outcome = this.readItemStack( buffer );

		return new SmeltingRecipe( input, outcome, null );
	}



}
