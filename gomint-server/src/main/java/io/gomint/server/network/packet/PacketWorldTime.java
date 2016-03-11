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
public class PacketWorldTime extends Packet {

	private int ticks;
	private boolean counting;

	public PacketWorldTime() {
		super( Protocol.WORLD_TIME_PACKET );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeInt( this.ticks );
		buffer.writeBoolean( this.counting );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.ticks = buffer.readInt();
		this.counting = buffer.readBoolean();
	}

	@Override
	public int estimateLength() {
		return 5;
	}
}
