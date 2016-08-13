/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.taglib.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class Packet {

    /**
     * Internal MC:PE id of this packet
     */
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

	protected ItemStack readItemStack( PacketBuffer buffer ) {
		short id = buffer.readShort();
		if ( id == 0 ) {
			return new ItemStack( 0, (short) 0, 0, null );
		}

		byte  amount = buffer.readByte();
		short data   = buffer.readShort();

		NBTTagCompound nbt      = null;
		short          extraLen = buffer.readLShort();
		if ( extraLen > 0 ) {
			ByteArrayInputStream bin = new ByteArrayInputStream( buffer.getBuffer(), buffer.getPosition(), extraLen );
			try {
				nbt = NBTTagCompound.readFrom( bin, false, ByteOrder.LITTLE_ENDIAN );
			} catch ( IOException e ) {
				e.printStackTrace();
			}

			buffer.skip( extraLen );
		}

		return new ItemStack( id, data, amount, nbt );
	}

	protected void writeItemStack( ItemStack itemStack, PacketBuffer buffer ) {
		buffer.writeShort( itemStack.getId() );
		if ( itemStack.getId() > 0 ) {
			buffer.writeByte( itemStack.getAmount() );
			buffer.writeShort( itemStack.getData() );

			if ( itemStack.getNbtData() == null ) {
                buffer.writeLShort( (short) 0 );
			} else {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    itemStack.getNbtData().writeTo( byteArrayOutputStream, false, ByteOrder.LITTLE_ENDIAN );
                    buffer.writeLShort( (short) byteArrayOutputStream.size() );
                    buffer.writeBytes( byteArrayOutputStream.toByteArray() );
                } catch ( IOException e ) {
                    e.printStackTrace();
                    buffer.writeLShort( (short) 0 );
                }
            }
		}
	}

}
