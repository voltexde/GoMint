/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.leveldb;

import io.gomint.server.async.Delegate2;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.world.Block;

import java.util.Collection;

/**
 * @author geNAZt
 * @version 1.0
 */
public class LevelDBChunk extends ChunkAdapter {

	@Override
	public void packageChunk( Delegate2<Long, Packet> callback ) {

	}

	@Override
	public boolean canBeGCed() {
		return false;
	}

	@Override
	public Collection<EntityPlayer> getPlayers() {
		return null;
	}

	@Override
	public Block getBlockAt( int x, int y, int z ) {
		return null;
	}

}
