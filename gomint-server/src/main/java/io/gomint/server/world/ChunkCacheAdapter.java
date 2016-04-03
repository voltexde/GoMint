/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class ChunkCacheAdapter {

	// CHECKSTYLE:OFF
	// ==================================== FIELDS ==================================== //
	protected final WorldAdapter              world;
	protected final LongObjMap<ChunkAdapter> cachedChunks;
	protected       boolean                enableAutoSave;
	protected       long                   autoSaveInterval;


	protected ChunkCacheAdapter( WorldAdapter world ) {
		this.world = world;
		this.cachedChunks = HashLongObjMaps.newMutableMap();
	}
	// CHECKSTYLE:ON

	/**
	 * Checks whether or not the specified chunk is currently in cache.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 * @return Whether or not the specified chunk is currently in cache
	 */
	public abstract boolean hasChunk( int x, int z );

	/**
	 * Attempts to get the specified chunk.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 * @return The chunk if it is loaded or null otherwise
	 */
	public abstract ChunkAdapter getChunk( int x, int z );

    /**
     * Ticking for Chunk GC
     *
     * @param currentTimeMS The current time in milliseconds. Used to reduce the number of calls to System#currentTimeMillis()
     */
    public abstract void tick( long currentTimeMS );

	/**
	 * Sets whether this Cache should enable automatic chunk saving
	 *
	 * @param autoSave  Boolean which decides whether thich cache autosaves or not
	 */
	public void setEnableAutosave( boolean autoSave ) {
		this.enableAutoSave = autoSave;
	}
}
