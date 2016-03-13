/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.async.Delegate;
import io.gomint.server.network.packet.Packet;
import io.gomint.world.Chunk;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class ChunkAdapter implements Chunk {

	/**
	 * Makes a request to package this chunk asynchronously. The package that will be
	 * given to the provided callback will be a world chunk packet inside a batch packet.
	 * <p>
	 * This operation is done asynchronously in order to limit how many chunks are being
	 * packaged in parallel as well as to cache some chunk packets.
	 *
	 * @param callback The callback to be invoked once the operation is complete
	 */
	public abstract void packageChunk( Delegate<Packet> callback );

}
