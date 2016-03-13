/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.async.Delegate;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class AsyncChunkLoadTask extends AsyncChunkTask {

	private int x;
	private int z;
	private boolean generate;
	private Delegate<ChunkAdapter> callback;

	public AsyncChunkLoadTask( int x, int z, boolean generate, Delegate<ChunkAdapter> callback ) {
		super( Type.LOAD );
		this.x = x;
		this.z = z;
		this.generate = generate;
		this.callback = callback;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public boolean isGenerate() {
		return generate;
	}

	public Delegate<ChunkAdapter> getCallback() {
		return callback;
	}

}
