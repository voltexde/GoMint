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
public class PacketB9 extends Packet {

	private static final byte[] CONTENTS = new byte[] { (byte) 0x78, (byte) 0x00, (byte) 0x04, (byte) 0x00,
	                                                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                                    (byte) 0x00 };

	public PacketB9() {
		super( Protocol.PACKET_B9 );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeBytes( CONTENTS );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		buffer.skip( CONTENTS.length );
	}

	@Override
	public int estimateLength() {
		return CONTENTS.length;
	}
}
