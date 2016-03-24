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

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

/**
 * A recipe of some type that may be used to create a new item given some other
 * ingredients.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class Recipe {

	private UUID uuid;

	protected Recipe( UUID uuid ) {
		this.uuid = ( uuid != null ? uuid : UUID.randomUUID() );
	}

	/**
	 * Gets the UUID of this recipe.
	 *
	 * @return The UUID of this recipe
	 */
	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Returns a list of ingredients required by this recipe.
	 *
	 * @return The list of ingredients required by this recipe
	 */
	public abstract Collection<ItemStack> getIngredients();

	/**
	 * Creates a new item stack resembling the result of this recipe.
	 * The returned item stack is ready for use and does not need to be
	 * cloned before further usage.
	 *
	 * @return The newly created resulting item stack
	 */
	public abstract ItemStack createResult();

	/**
	 * Serializes the recipe into the given packet buffer.
	 *
	 * @param buffer The buffer to serialize the recipe into
	 * @param intermediate Packet data output stream for intermediate data (internal optimization)
	 *
	 * @throws IOException Thrown if an I/O error occurs whilst serializing the recipe
	 */
	public abstract void serialize( PacketBuffer buffer, PacketDataOutputStream intermediate ) throws IOException;

}
