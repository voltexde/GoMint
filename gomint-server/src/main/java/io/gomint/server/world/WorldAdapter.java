/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.async.Delegate;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.world.World;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class WorldAdapter implements World {

	/**
	 * Adds a new player to this world and schedules all world chunk packets required for spawning
	 * the player for send.
	 *
	 * @param player The player entity to add to the world
	 */
	public abstract void addPlayer( EntityPlayer player );

    /**
     * Removes a player from this world and cleans up its references
     *
     * @param player The player entity which should be removed from the world
     */
    public abstract void removePlayer( EntityPlayer player );

	/**
	 * Ticks the world and updates what needs to be updated.
	 */
	public abstract void tick();

	/**
	 * Gets the chunk at the specified coordinates. If the chunk is currently not available
	 * it will be loaded or generated.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 * @return The chunk if available or null otherwise
	 */
	public abstract ChunkAdapter getChunk( int x, int z );

	/**
	 * Gets a chunk asynchronously. This allows to load or generate the chunk if it is not yet available
	 * and then return it once it gets available. The callback is guaranteed to be invoked: if the chunk
	 * could not be loaded nor be generated it will be passed null as its argument.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 * @param generate Whether or not to generate teh chunk if it does not yet exist
	 * @param callback The callback to be invoked once the chunk is available
	 */
	public abstract void getOrLoadChunk( int x, int z, boolean generate, Delegate<ChunkAdapter> callback );

    /**
     * Send a chunk of this world to the client
     *
     * @param x The x-coordinate of the chunk
     * @param z The z-coordinate of the chunk
     * @param player The player we want to send the chunk to
     */
    public abstract void sendChunk( int x, int z, EntityPlayer player );

    /**
     * Move a player to a new chunk. This is done so we know which player is in which chunk so we can unload unneeded
     * Chunks better and faster.
     *
     * @param x The x-coordinate of the chunk
     * @param z The z-coordinate of the chunk
     * @param player The player which should be set into the chunk
     */
    public abstract void movePlayerToChunk( int x, int z, EntityPlayer player );

}
