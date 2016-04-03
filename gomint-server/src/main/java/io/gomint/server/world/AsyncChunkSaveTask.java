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
public class AsyncChunkSaveTask extends AsyncChunkTask {

	private final ChunkAdapter chunk;

	public AsyncChunkSaveTask( ChunkAdapter chunk ) {
		super( Type.SAVE );
		this.chunk = chunk;
	}

	public ChunkAdapter getChunk() {
		return this.chunk;
	}

}
