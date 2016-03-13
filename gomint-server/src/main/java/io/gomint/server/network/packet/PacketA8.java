/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author BlackyPaw
 * @version 1.0
 */
@Data
@EqualsAndHashCode( callSuper = false )
public class PacketA8 extends Packet {

	private long entityId;

	public PacketA8() {
		super( Protocol.PACKET_A8 );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeLong( this.entityId );
		buffer.writeLong( 0 );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.entityId = buffer.readLong();
		buffer.skip( 8 );
	}

	@Override
	public int estimateLength() {
		return 16;
	}
}
