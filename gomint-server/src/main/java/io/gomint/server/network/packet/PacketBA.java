/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class PacketBA extends Packet {

	public PacketBA() {
		super( Protocol.PACKET_BA );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		// Seemingly the first 4 bytes indicate the length of an array
		// that follow; but its actual contents still remain a mystery...
		buffer.writeInt( 0 );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		// Cannot read as actual format is still unknown
	}

	@Override
	public int estimateLength() {
		return 4;
	}
}
