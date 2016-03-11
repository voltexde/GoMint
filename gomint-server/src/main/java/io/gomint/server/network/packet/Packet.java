/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class Packet {

	protected final byte id;

	protected Packet( byte id ) {
		this.id = id;
	}

	/**
	 * Gets the packet's ID.
	 *
	 * @return The packet's ID
	 */
	public byte getId() {
		return this.id;
	}

	/**
	 * Serializes this packet into the given buffer.
	 *
	 * @param buffer The buffer to serialize this packet into
	 */
	public abstract void serialize( PacketBuffer buffer );

	/**
	 * Deserializes this packet from the given buffer.
	 *
	 * @param buffer The buffer to deserialize this packet from
	 */
	public abstract void deserialize( PacketBuffer buffer );

	/**
	 * Returns an estimate length of the packet (used for pre-allocation).
	 *
	 * @return The estimate length of the packet or -1 if unknown
	 */
	public int estimateLength() {
		return -1;
	}

	/**
	 * Returns the ordering channel to send the packet on.
	 *
	 * @return The ordering channel of the packet
	 */
	public int orderingChannel() {
		return 0;
	}

}
