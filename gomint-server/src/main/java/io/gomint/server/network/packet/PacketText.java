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
public class PacketText extends Packet {

	public enum Type {

		/**
		 * Type value for unformatted messages.
		 */
		SYSTEM( (byte) 0 ),

		/**
		 * Type value for usual player chat.
		 */
		PLAYER_CHAT( (byte) 1 ),

		/**
		 * Type value for localizable messages included in Minecraft's language files.
		 */
		LOCALIZABLE( (byte) 2 ),

		/**
		 * Type value for displaying text right above a player's action bar.
		 */
		HOTBAR( (byte) 3 ),

		/**
		 * Type value for displaying text slightly below the center of the screen (similar to title
		 * text of PC edition).
		 */
		TITLE( (byte) 4 ),

		/**
		 * Type value for unformatted messages. Actual use unknown, same as system, apparently.
		 */
		UNKNOWN( (byte) 5 );

		public static Type getById( byte id ) {
			switch ( id ) {
				case 0:
					return SYSTEM;
				case 1:
					return PLAYER_CHAT;
				case 2:
					return LOCALIZABLE;
				case 3:
					return HOTBAR;
				case 4:
					return TITLE;
				case 5:
					return UNKNOWN;
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
	private String[] arguments;

	public PacketText() {
		super( Protocol.PACKET_TEXT );
	}

	/**
	 * Shorthand constructor for PLAYER_CHAT messages.
	 *
	 * @param sender The sender of the chat message
	 * @param message The actual chat message
	 */
	public PacketText( String sender, String message ) {
		this();
		this.type = Type.PLAYER_CHAT;
		this.sender = sender;
		this.message = message;
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeByte( this.type.getId() );
		switch ( this.type ) {
			case SYSTEM:
			case TITLE:
			case UNKNOWN:
				buffer.writeString( this.message );
				break;

			case PLAYER_CHAT:
				buffer.writeString( this.sender );
				buffer.writeString( this.message );
				break;

			case LOCALIZABLE:
			case HOTBAR:
				buffer.writeString( this.message );
				buffer.writeByte( (byte) this.arguments.length );
				for ( int i = 0; i < this.arguments.length; ++i ) {
					buffer.writeString( this.arguments[i] );
				}
				break;
		}
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.type = Type.getById( buffer.readByte() );
		switch ( this.type ) {
			case SYSTEM:
			case TITLE:
			case UNKNOWN:
				this.message = buffer.readString();
				break;

			case PLAYER_CHAT:
				this.sender = buffer.readString();
				this.message = buffer.readString();
				break;

			case LOCALIZABLE:
			case HOTBAR:
				this.message = buffer.readString();
				byte count = buffer.readByte();
				this.arguments = new String[count];
				for ( byte i = 0; i < count; ++i ) {
					arguments[i] = buffer.readString();
				}
				break;
		}
	}
}
