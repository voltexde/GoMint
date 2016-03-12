/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network;

import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketEntityMetadata;
import io.gomint.server.network.packet.PacketEntityMotion;
import io.gomint.server.network.packet.PacketEntityMovement;
import io.gomint.server.network.packet.PacketLogin;
import io.gomint.server.network.packet.PacketPlayState;
import io.gomint.server.network.packet.PacketPlayerMovement;
import io.gomint.server.network.packet.PacketWorldChunk;
import io.gomint.server.network.packet.PacketWorldTime;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public final class Protocol {

	// ========================================= PACKET IDS ========================================= //
	public static final byte PACKET_LOGIN           = (byte) 0x8F;
	public static final byte PACKET_BATCH           = (byte) 0x92;
	public static final byte PACKET_PLAY_STATE      = (byte) 0x90;
	public static final byte PACKET_WORLD_TIME      = (byte) 0x94;
	public static final byte PACKET_ENTITY_MOVEMENT = (byte) 0x9C;
	public static final byte PACKET_PLAYER_MOVEMENT = (byte) 0x9D;
	public static final byte PACKET_ENTITY_METADATA = (byte) 0xAD;
	public static final byte PACKET_ENTITY_MOTION   = (byte) 0xAE;
	public static final byte PACKET_WORLD_CHUNK     = (byte) 0xBF;

	// ========================================= PACKET METHODS ========================================= //

	/**
	 * Creates a new packet instance given the packet ID found inside the first byte of any
	 * packet's data.
	 *
	 * @param id The ID of the the packet to create
	 *
	 * @return The created packet or null if it could not be created
	 */
	public static Packet createPacket( byte id ) {
		switch ( id ) {
			case PACKET_LOGIN:
				return new PacketLogin();

			case PACKET_PLAY_STATE:
				return new PacketPlayState();

			case PACKET_WORLD_TIME:
				return new PacketWorldTime();

			case PACKET_ENTITY_MOVEMENT:
				return new PacketEntityMovement();

			case PACKET_PLAYER_MOVEMENT:
				return new PacketPlayerMovement();

			case PACKET_ENTITY_METADATA:
				return new PacketEntityMetadata();

			case PACKET_ENTITY_MOTION:
				return new PacketEntityMotion();

			case PACKET_WORLD_CHUNK:
				return new PacketWorldChunk();

			default:
				return null;
		}
	}


	private Protocol() {
		throw new AssertionError( "Cannot instantiate Protocol!" );
	}

}
