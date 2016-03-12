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
public class PacketEntityMovement extends Packet {

	private long[]  entityId;
	private float[] x;
	private float[] y;
	private float[] z;
	private float[] yaw;
	private float[] headYaw;
	private float[] pitch;

	public PacketEntityMovement() {
		super( Protocol.PACKET_ENTITY_MOVEMENT );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		// Assert pre-condition:
		assert this.entityId.length == this.x.length &&
		       this.entityId.length == this.y.length &&
		       this.entityId.length == this.z.length &&
		       this.entityId.length == this.yaw.length &&
		       this.entityId.length == this.headYaw.length &&
		       this.entityId.length == this.pitch.length : "Entity Movement batch arrays must not have different sizes";

		buffer.writeInt( this.entityId.length );
		for ( int i = 0; i < this.entityId.length; ++i ) {
			buffer.writeLong( this.entityId[i] );
			buffer.writeFloat( this.x[i] );
			buffer.writeFloat( this.y[i] );
			buffer.writeFloat( this.z[i] );
			buffer.writeFloat( this.yaw[i] );
			buffer.writeFloat( this.headYaw[i] );
			buffer.writeFloat( this.pitch[i] );
		}
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		int count = buffer.readInt();

		this.entityId = new long[count];
		this.x = new float[count];
		this.y = new float[count];
		this.z = new float[count];
		this.yaw = new float[count];
		this.headYaw = new float[count];
		this.pitch = new float[count];

		for ( int i = 0; i < count; ++i ) {
			this.entityId[i] = buffer.readLong();
			this.x[i] = buffer.readFloat();
			this.y[i] = buffer.readFloat();
			this.z[i] = buffer.readFloat();
			this.yaw[i] = buffer.readFloat();
			this.headYaw[i] = buffer.readFloat();
			this.pitch[i] = buffer.readFloat();
		}
	}

	@Override
	public int estimateLength() {
		return ( this.entityId.length << 5 ); // Count * 32 bytes
	}
}
