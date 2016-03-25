/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.crafting;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.GoMintServer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBatch;
import io.gomint.server.network.packet.PacketCraftingRecipes;
import io.gomint.taglib.NBTTagCompound;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.Deflater;

/**
 * Helper class used to manage all available crafting recipes.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class RecipeManager {

	private final GoMintServer gomint;
	private Set<Recipe> recipes;

	private Packet batchPacket;
	private boolean dirty;

	/**
	 * Constructs a new recipe manager.
	 *
	 * @param gomint The GoMint server instance the recipe manager belongs to
	 */
	public RecipeManager( GoMintServer gomint ) {
		this.gomint = gomint;
		this.recipes = new HashSet<>();
		this.dirty = true;
	}

	/**
	 * Gets a packet containing all crafting recipes that may be sent to players in
	 * order to let them know what crafting recipes are supported by the server.
	 *
	 * @return The packet containing all crafting recipes
	 */
	public Packet getCraftingRecipesBatch() {
		if ( this.dirty ) {
			PacketCraftingRecipes recipes = new PacketCraftingRecipes();
			recipes.setRecipes( this.recipes );
			PacketBuffer buffer = new PacketBuffer( 16384 );
			buffer.writeByte( recipes.getId() );
			recipes.serialize( buffer );

			byte[] rawData;

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try ( DataOutputStream dout = new DataOutputStream( bout ) ) {
				dout.writeInt( buffer.getPosition() - buffer.getBufferOffset() );
				dout.write( buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset() );
				dout.flush();

				rawData = bout.toByteArray();
			} catch ( IOException e ) {
				throw new RuntimeException( "Failed to batch crafting recipes!" );
			}

			Deflater deflater = new Deflater();
			deflater.setInput( rawData );
			deflater.finish();

			bout.reset();
			byte[] intermediate = new byte[1024];
			while ( !deflater.finished() ) {
				int read = deflater.deflate( intermediate );
				bout.write( intermediate, 0, read );
			}

			PacketBatch batch = new PacketBatch();
			batch.setPayload( bout.toByteArray() );

			this.batchPacket = batch;
			this.dirty = false;
		}

		return this.batchPacket;
	}

	/**
	 * Registers the given crafting recipe thus making it available for crafting
	 * from now on.
	 *
	 * @param recipe The recipe to register
	 */
	public void registerRecipe( Recipe recipe ) {
		this.recipes.add( recipe );
		this.dirty = true;
	}

}
