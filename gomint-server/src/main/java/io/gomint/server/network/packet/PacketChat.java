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
public class PacketChat extends Packet {

	public enum Type {

		/**
		 * Type value for usual player chat.
		 */
		PLAYER_CHAT( (byte) 1 );

		public static Type getById( byte id ) {
			switch ( id ) {
				case 1:
					return PLAYER_CHAT;
				default:
					return null;
			}
		}

		private final byte id;

		Type( byte id ) {
			this.id = id;
		}

		public byte getId() {
			return this.id;
		}

	}

	private Type type;
	private String sender;
	private String message;

	public PacketChat() {
		super( Protocol.PACKET_CHAT );
	}

	/**
	 * Shorthand constructor for PLAYER_CHAT messages.
	 *
	 * @param sender The sender of the chat message
	 * @param message The actual chat message
	 */
	public PacketChat( String sender, String message ) {
		this();
		this.type = Type.PLAYER_CHAT;
		this.sender = sender;
		this.message = message;
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeByte( this.type.getId() );
		switch ( this.type ) {
			case PLAYER_CHAT:
				buffer.writeString( this.sender );
				buffer.writeString( this.message );
				break;
		}
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.type = Type.getById( buffer.readByte() );
		switch ( this.type ) {
			case PLAYER_CHAT:
				this.sender = buffer.readString();
				this.message = buffer.readString();
				break;
		}
	}
}
