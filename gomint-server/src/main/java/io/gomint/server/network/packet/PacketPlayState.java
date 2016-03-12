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
public class PacketPlayState extends Packet {

	/**
	 * Enumeration of play states observed to be sent inside certain packets.
	 */
	public enum PlayState {

		HANDSHAKE( 0 ),
		SPAWN( 3 );

		public static PlayState fromValue( int value ) {
			switch ( value ) {
				case 0:
					return HANDSHAKE;
				case 3:
					return SPAWN;
				default:
					return null;
			}
		}

		private int value;

		PlayState( int value ) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

	}

	private PlayState state;

	public PacketPlayState() {
		super( Protocol.PACKET_PLAY_STATE );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeInt( this.state.getValue() );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.state = PlayState.fromValue( buffer.readInt() );
	}

	@Override
	public int estimateLength() {
		return 4;
	}
}
