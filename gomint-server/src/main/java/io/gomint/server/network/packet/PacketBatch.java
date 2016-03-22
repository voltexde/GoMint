/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import io.gomint.server.util.SerializableByteArrayOutputstream;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author BlackyPaw
 * @version 1.0
 */
@Data
@EqualsAndHashCode( callSuper = false )
public class PacketBatch extends Packet {

	private byte[] payload;
    private SerializableByteArrayOutputstream byteArrayOutputstream;

	public PacketBatch() {
		super( Protocol.PACKET_BATCH );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
        if ( byteArrayOutputstream != null ) {
            buffer.writeInt( this.byteArrayOutputstream.size() );

            for ( int i = 0; i < this.byteArrayOutputstream.size(); i++ ) {
                buffer.writeByte( this.byteArrayOutputstream.getByte( i ) );
            }
        } else {
            buffer.writeInt( this.payload.length );
            buffer.writeBytes( this.payload );
        }
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		int length = buffer.readInt();
		this.payload = new byte[length];
		buffer.readBytes( this.payload );
	}

	@Override
	public int estimateLength() {
		return 4 + ( this.byteArrayOutputstream != null ? this.byteArrayOutputstream.size() : this.payload.length );
	}

}
