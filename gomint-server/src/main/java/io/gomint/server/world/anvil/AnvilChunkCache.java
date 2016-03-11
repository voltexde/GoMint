/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.ChunkCacheAdapter;
import io.gomint.server.world.ChunkCoordinatePair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class AnvilChunkCache extends ChunkCacheAdapter {

	// ==================================== FIELDS ==================================== //
	private Map<ChunkCoordinatePair, AnvilChunk> cachedChunks = new HashMap<>();

	public AnvilChunkCache() {

	}

	// ==================================== CHUNK CACHE ==================================== //
	@Override
	public void requestChunk( int x, int z ) {
		// TODO: Implement dynamic chunk loading here
	}

	@Override
	public boolean hasChunk( int x, int z ) {
		return this.cachedChunks.containsKey( new ChunkCoordinatePair( x, z ) );
	}

	@Override
	public ChunkAdapter getChunk( int x, int z ) {
		return this.cachedChunks.get( new ChunkCoordinatePair( x, z ) );
	}

	/**
	 * Puts the specified chunk into the cache thus making it available to the outside
	 *
	 * @param chunk The chunk to put into the cache
	 */
	void putChunk( AnvilChunk chunk ) {
		this.cachedChunks.put( new ChunkCoordinatePair( chunk.getX(), chunk.getZ() ), chunk );
	}
}
