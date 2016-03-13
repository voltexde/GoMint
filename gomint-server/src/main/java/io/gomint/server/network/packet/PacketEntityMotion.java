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
public class PacketEntityMotion extends Packet {

	private long[]  entityId;
	private float[] velocityX;
	private float[] velocityY;
	private float[] velocityZ;

	public PacketEntityMotion() {
		super( Protocol.PACKET_ENTITY_MOTION );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		assert this.entityId.length == this.velocityX.length &&
		       this.entityId.length == this.velocityY.length &&
		       this.entityId.length == this.velocityZ.length
				: "Entity motion batch arrays must not have different sizes";

		buffer.writeInt( this.entityId.length );
		for ( int i = 0; i < this.entityId.length; ++i ) {
			buffer.writeLong( this.entityId[i] );
			buffer.writeFloat( this.velocityX[i] );
			buffer.writeFloat( this.velocityY[i] );
			buffer.writeFloat( this.velocityZ[i] );
		}
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		int count = buffer.readInt();

		this.entityId = new long[count];
		this.velocityX = new float[count];
		this.velocityY = new float[count];
		this.velocityZ = new float[count];

		for ( int i = 0; i < count; ++i ) {
			this.entityId[i] = buffer.readLong();
			this.velocityX[i] = buffer.readFloat();
			this.velocityY[i] = buffer.readFloat();
			this.velocityZ[i] = buffer.readFloat();
		}
	}
}
