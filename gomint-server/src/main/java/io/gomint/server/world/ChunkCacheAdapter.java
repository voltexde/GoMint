/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class ChunkCacheAdapter {

	/**
	 * Requests loading of the specified chunk. The chunk will be loaded as soon as possible
	 * by the underlying world implementation.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 */
	public abstract void requestChunk( int x, int z );

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

}
