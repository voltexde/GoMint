/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.async.Delegate;
import io.gomint.server.network.packet.Packet;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class AsyncChunkPackageTask extends AsyncChunkTask {

	private ChunkAdapter chunk;
	private Delegate<Packet> callback;

	public AsyncChunkPackageTask( ChunkAdapter chunk, Delegate<Packet> callback ) {
		super( Type.PACKAGE );
		this.chunk = chunk;
		this.callback = callback;
	}

	public ChunkAdapter getChunk() {
		return chunk;
	}

	public Delegate<Packet> getCallback() {
		return callback;
	}
}
