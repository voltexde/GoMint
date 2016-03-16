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
public class PacketWorldChunk extends Packet {

	private int x;
	private int z;
	private byte[] data;

	public PacketWorldChunk() {
		super( Protocol.PACKET_WORLD_CHUNK );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeInt( this.x );
		buffer.writeInt( this.z );
		buffer.writeByte( (byte) 0x00 );
		buffer.writeInt( this.data.length );
		buffer.writeBytes( data );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.x = buffer.readInt();
		this.z = buffer.readInt();
		buffer.skip( 1 );
		int length = buffer.readInt();
		this.data = new byte[length];
		buffer.readBytes( this.data );
	}

	@Override
	public int estimateLength() {
		return 13 + ( this.data == null ? 0 : this.data.length );
	}

    @Override
    public String toString() {
        return "PacketWorldChunk(x=" + x + ";z="+ z + ";dataHash=" + hashPayload( data ) + ")";
    }

    private long hashPayload( byte[] array ) {
        long h = 0;

        for ( byte aPayload : array ) {
            h = 31 * h + aPayload;
        }

        return h;
    }

}
