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
public class PacketPlayerMovement extends Packet {

	private long entityId;
	private float x;
	private float y;
	private float z;
	private float yaw;
	private float headYaw;          // Always equal to yaw; only differs for animals (see PacketEntityMovement)
	private float pitch;
	private boolean teleport;
	private boolean onGround;

	public PacketPlayerMovement() {
		super( Protocol.PACKET_PLAYER_MOVEMENT );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeLong( this.entityId );
		buffer.writeFloat( this.x );
		buffer.writeFloat( this.y );
		buffer.writeFloat( this.z );
		buffer.writeFloat( this.yaw );
		buffer.writeFloat( this.headYaw );
		buffer.writeFloat( this.pitch );
		buffer.writeBoolean( this.teleport );
		buffer.writeBoolean( this.onGround );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.entityId = buffer.readLong();
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		this.yaw = buffer.readFloat();
		this.headYaw = buffer.readFloat();
		this.pitch = buffer.readFloat();
		this.teleport = buffer.readBoolean();
		this.onGround = buffer.readBoolean();
	}

	@Override
	public int estimateLength() {
		return 34;
	}
}
