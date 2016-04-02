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
public class PacketWorldInitialization extends Packet {

	private int   seed;
	private byte  dimension;
	private int   generator;
	private int   gamemode;
	private long  entityId;
	private int   spawnX;
	private int   spawnY;
	private int   spawnZ;
	private float x;
	private float y;
	private float z;
	private boolean allowCheats;

	public PacketWorldInitialization() {
		super( Protocol.PACKET_WORLD_INITIALIZATION );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeInt( this.seed );
		buffer.writeByte( this.dimension );
		buffer.writeInt( this.generator );
		buffer.writeInt( this.gamemode );
		buffer.writeLong( this.entityId );
		buffer.writeInt( this.spawnX );
		buffer.writeInt( this.spawnY );
		buffer.writeInt( this.spawnZ );
		buffer.writeFloat( this.x );
		buffer.writeFloat( this.y );
		buffer.writeFloat( this.z );
		buffer.writeBoolean( this.allowCheats );
		buffer.writeBoolean( true );        // Unknown
		buffer.writeBoolean( false );       // Unknown
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.seed = buffer.readInt();
		this.dimension = buffer.readByte();
		this.generator = buffer.readInt();
		this.gamemode = buffer.readInt();
		this.entityId = buffer.readLong();
		this.spawnX = buffer.readInt();
		this.spawnY = buffer.readInt();
		this.spawnZ = buffer.readInt();
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		this.allowCheats = buffer.readBoolean();
		buffer.skip( 2 );
	}
}
