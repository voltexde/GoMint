/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.server.async.Delegate;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.ChunkCacheAdapter;
import io.gomint.server.world.ChunkCoordinatePair;
import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import net.openhft.koloboke.collect.set.ObjSet;

import java.util.Queue;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class AnvilChunkCache extends ChunkCacheAdapter {

	// ==================================== FIELDS ==================================== //
	private final AnvilWorldAdapter                                        world;
	private       ObjObjMap<ChunkCoordinatePair, AnvilChunk>               cachedChunks;

	public AnvilChunkCache( final AnvilWorldAdapter world ) {
		this.world = world;
		this.cachedChunks = HashObjObjMaps.newMutableMap();
	}

	// ==================================== CHUNK CACHE ==================================== //

	@Override
	public boolean hasChunk( int x, int z ) {
		return this.cachedChunks.containsKey( new ChunkCoordinatePair( x, z ) );
	}

	@Override
	public AnvilChunk getChunk( int x, int z ) {
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
