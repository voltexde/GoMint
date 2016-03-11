/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network;

import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketWorldTime;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public final class Protocol {

	// ========================================= PACKET IDS ========================================= //
	public static final byte LOGIN_PACKET      = (byte) 0x8f;
	public static final byte BATCH_PACKET      = (byte) 0x92;
	public static final byte WORLD_TIME_PACKET = (byte) 0x94;

	// ========================================= PACKET METHODS ========================================= //

	/**
	 * Creates an appropriate packet instance given the packet ID found inside the first byte of any
	 * packet's data.
	 *
	 * @param id The ID of the the packet to create
	 *
	 * @return The created packet or null if it could not be created
	 */
	public static Packet createPacket( byte id ) {
		switch ( id ) {
			case WORLD_TIME_PACKET:
				return new PacketWorldTime();

			default:
				return null;
		}
	}


	private Protocol() {
		throw new AssertionError( "Cannot instantiate Protocol!" );
	}

}
