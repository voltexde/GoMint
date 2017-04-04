/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import io.gomint.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class PacketDataOutputStream extends OutputStream {

    private ByteArrayOutputStream bout;
    private ByteBuffer buffer;

    public PacketDataOutputStream() {
        this( ByteOrder.BIG_ENDIAN );
    }

    public PacketDataOutputStream( ByteOrder order ) {
        this.bout = new ByteArrayOutputStream();
        this.buffer = ByteBuffer.allocate( 8 );
        this.buffer.order( order );
    }

    public void writeByte( byte val ) {
        this.write( val );
    }

    public void writeShort( short val ) {
        this.prepare();
        this.buffer.putShort( val );
        this.write( this.buffer.array(), this.buffer.arrayOffset(), 2 );
    }

    public void writeInt( int val ) {
        this.prepare();
        this.buffer.putInt( val );
        this.write( this.buffer.array(), this.buffer.arrayOffset(), 4 );
    }

    public void writeLong( long val ) {
        this.prepare();
        this.buffer.putLong( val );
        this.write( this.buffer.array(), this.buffer.arrayOffset(), 8 );
    }

    public void writeFloat( float val ) {
        this.prepare();
        this.buffer.putFloat( val );
        this.write( this.buffer.array(), this.buffer.arrayOffset(), 4 );
    }

    public void writeDouble( double val ) {
        this.prepare();
        this.buffer.putDouble( val );
        this.write( this.buffer.array(), this.buffer.arrayOffset(), 8 );
    }

    public void writeItemStack( ItemStack stack ) {
        this.writeShort( (short) stack.getId() );
        this.writeByte( (byte) stack.getAmount() );
        this.writeShort( stack.getData() );

        if ( stack.getNbtData() != null ) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try {
                stack.getNbtData().writeTo( bout, false, ByteOrder.LITTLE_ENDIAN );
                byte[] data = bout.toByteArray();
                this.writeShort( (short) data.length );
                this.write( data );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } else {
            this.writeShort( (short) 0 );
        }
    }

    public void writeUUID( UUID uuid ) {
        if ( this.buffer.order() == ByteOrder.BIG_ENDIAN ) {
            this.writeLong( uuid.getMostSignificantBits() );
            this.writeLong( uuid.getLeastSignificantBits() );
        } else {
            this.writeLong( uuid.getLeastSignificantBits() );
            this.writeLong( uuid.getMostSignificantBits() );
        }
    }

    @Override
    public void write( byte[] b ) {
        this.write( b, 0, b.length );
    }

    @Override
    public void write( byte[] b, int off, int len ) {
        this.bout.write( b, off, len );
    }

    @Override
    public void write( int b ) {
        this.bout.write( b );
    }

    @Override
    public void flush() throws IOException {
        this.bout.flush();
    }

    @Override
    public void close() throws IOException {
        this.bout.close();
    }

    public byte[] toByteArray() {
        return this.bout.toByteArray();
    }

    public void reset() {
        this.bout.reset();
    }

    private void prepare() {
        this.buffer.limit( this.buffer.capacity() );
        this.buffer.position( 0 );
    }

}
