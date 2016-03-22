/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.async.Delegate2;
import io.gomint.server.network.packet.Packet;
import lombok.Getter;

/**
 * @author BlackyPaw
 * @author geNAZt
 * @version 1.0
 */
@Getter
public class AsyncChunkPackageTask extends AsyncChunkTask {

	private int                     x;
    private int                     z;
    private Delegate2<Long, Packet> callback;

	public AsyncChunkPackageTask( int x, int z, Delegate2<Long, Packet> callback ) {
		super( Type.PACKAGE );
		this.x = x;
        this.z = z;
		this.callback = callback;
	}

}
