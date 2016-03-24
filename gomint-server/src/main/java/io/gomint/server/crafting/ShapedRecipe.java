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
import net.openhft.koloboke.collect.map.IntObjCursor;
import net.openhft.koloboke.collect.map.IntObjMap;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Resembles a shaped crafting recipe, i.e. a recipe that requires its
 * arrangement to be arranged in specific way and does not accept work
 * if any ingredient is not in the right spot.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class ShapedRecipe extends CraftingRecipe {

	private int width;
	private int height;

	private ItemStack[] arrangement;
	private ItemStack   outcome;

	private Collection<ItemStack> ingredients;

	public ShapedRecipe( int width, int height, ItemStack[] ingredients, ItemStack outcome, UUID uuid ) {
		super( uuid );
		assert ingredients.length == width * height : "Invalid arrangement: Fill out empty slots with air!";

		this.width = width;
		this.height = height;
		this.arrangement = ingredients;
		this.outcome = outcome;
	}

	/**
	 * Gets the width of this shaped recipe.
	 *
	 * @return The width of this shaped recipe
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Gets the height of this shaped recipe.
	 *
	 * @return The height of this shaped recipe
	 */
	public int getHeight() {
		return this.height;
	}

	@Override
	public Collection<ItemStack> getIngredients() {
		if ( this.ingredients == null ) {
			// Got to sort out possible AIR slots and combine types:
			IntObjMap<ItemStack> map = HashIntObjMaps.newMutableMap( this.width * this.height );
			for ( int j = 0; j < this.height; ++j ) {
				for ( int i = 0; i < this.width; ++i ) {
					ItemStack stack = this.arrangement[j * this.width + i];
					if ( stack.getId() != 0 ) {
						if ( !map.containsKey( stack.getId() ) ) {
							// Make sure to keep NBT data:
							ItemStack clone = stack.clone();
							clone.setAmount( 0 );
							map.put( stack.getId(), clone );
						}

						ItemStack combined = map.get( stack.getId() );
						combined.setAmount( combined.getAmount() + stack.getAmount() );
					}
				}
			}

			this.ingredients = new ArrayList<>( map.size() );
			IntObjCursor<ItemStack> cursor = map.cursor();
			while ( cursor.moveNext() ) {
				this.ingredients.add( cursor.value() );
			}
		}
		return this.ingredients;
	}

	@Override
	public ItemStack createResult() {
		return this.outcome.clone();
	}

	@Override
	public void serialize( PacketBuffer buffer, PacketDataOutputStream intermediate ) throws IOException {
		intermediate.writeInt( this.width );
		intermediate.writeInt( this.height );

		for ( int i = 0; i < this.arrangement.length; ++i ) {
			ItemStack stack = this.arrangement[i];
			if ( stack.getId() == 0 ) {
				intermediate.writeShort( (short) 0 );
			} else {
				intermediate.writeItemStack( stack );
			}
		}

		intermediate.writeInt( 1 );             // Unknown use
		intermediate.writeItemStack( this.outcome );
		intermediate.writeUUID( this.getUUID() );

		byte[] recipeData = intermediate.toByteArray();
		buffer.writeInt( 1 );                   // Type
		buffer.writeInt( recipeData.length );   // Data Length
		buffer.writeBytes( recipeData );
	}

	/*              Left in here because it might get relevant in the future again
	@Override
	public boolean applies( CraftingContainer container, boolean consume ) {
		// Early out:
		if ( this.width > container.getWidth() || this.height > container.getHeight() ) {
			return false;
		}

		for ( int j = 0; j <= ( container.getHeight() - this.height ); ++j ) {
			for ( int i = 0; i <= ( container.getWidth() - this.width ); ++i ) {
				// i, j <=> Offset into container

				boolean applies = true;
				for ( int l = 0; l < this.height; ++l ) {
					for ( int k = 0; k < this.width; ++k ) {
						// k, l <=> Offset into recipe

						ItemStack required = this.arrangement[l * this.width + k];
						ItemStack found    = container.getCraftingSlot( i + k, j + l );

						if ( found != null ) {
							if ( required.getId() != found.getId() ||
							     required.getAmount() > found.getAmount() ||
							     ( required.getData() != (short) 0xFFFF && required.getData() != found.getData() ) ) {
								applies = false;
								break;
							}
						} else if ( required.getId() != 0 ) {
							applies = false;
							break;
						}
					}
					if ( !applies ) {
						break;
					}
				}

				if ( applies ) {
					for ( int l = 0; l < this.height; ++l ) {
						for ( int k = 0; k < this.width; ++k ) {
							// k, l <=> Offset into recipe

							ItemStack required = this.arrangement[l * this.width + k];
							ItemStack found    = container.getCraftingSlot( i + k, j + l );

							// Consume item:
							if ( found != null ) {
								found.setAmount( found.getAmount() - required.getAmount() );
								if ( found.getAmount() <= 0 ) {
									// Clear out slot:
									container.setCraftingSlot( i + k, j + l, null );
								}
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}
	*/
}
