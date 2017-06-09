/*
 *  Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 *  This code is licensed under the BSD license found in the
 *  LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.EncryptionHandler;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BatchUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger( BatchUtil.class );

    private static final ObjectBuffer<BatchStreamHolder> STREAM_BUFFER = new ObjectBuffer<>( 5 );
    private static long lastChecked = System.currentTimeMillis();

    /**
     * Batch multiple packets together and zlib compress them
     *
     * @param encryptionHandler The encryption the connection uses
     * @param packets           The packets which should be batched
     * @return the completely ready to be sent batch packet
     */
    public static PacketBatch batch( EncryptionHandler encryptionHandler, Collection<Packet> packets ) {
        if ( System.currentTimeMillis() - lastChecked >= TimeUnit.SECONDS.toMillis( 10 ) ) {
            STREAM_BUFFER.recalc();
        }

        BatchStreamHolder batchStreamHolder = STREAM_BUFFER.get();
        if ( batchStreamHolder == null ) {
            batchStreamHolder = new BatchStreamHolder();
        }

        for ( Packet packet : packets ) {
            int estimate = packet.estimateLength();
            if ( estimate == -1 ) {
                LOGGER.warn( "Packet " + packet.getClass().getSimpleName() + " has returned negative estimation" );
            }

            PacketBuffer buffer = new PacketBuffer( estimate == -1 ? 64 : estimate );
            buffer.writeByte( packet.getId() );
            packet.serialize( buffer );

            try {
                writeVarInt( buffer.getPosition(), batchStreamHolder.getOutputStream() );
                batchStreamHolder.getOutputStream().write( buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset() );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        PacketBatch batch = new PacketBatch();
        batch.setPayload( batchStreamHolder.getBytes() );

        if ( encryptionHandler != null ) {
            batch.setPayload( encryptionHandler.encryptInputForClient( batch.getPayload() ) );
        }

        batchStreamHolder.reset();
        STREAM_BUFFER.push( batchStreamHolder );

        return batch;
    }

    public static void writeVarInt( int value, OutputStream stream ) throws IOException {
        while ( ( value & -128 ) != 0 ) {
            stream.write( value & 127 | 128 );
            value >>>= 7;
        }

        stream.write( value );
    }

    public static PacketBatch batch( EncryptionHandler encryptionHandler, Packet... packets ) {
        return batch( encryptionHandler, Arrays.asList( packets ) );
    }

    public static class BatchStreamHolder {

        private ByteArrayOutputStream bout;
        private final Deflater deflater;

        public BatchStreamHolder() {
            this.bout = new ByteArrayOutputStream();
            this.deflater = new Deflater();
        }

        public void reset() {
            this.bout = new ByteArrayOutputStream();
            this.deflater.reset();
            this.deflater.setInput( new byte[0] );
        }

        public OutputStream getOutputStream() {
            return this.bout;
        }

        public byte[] getBytes() {
            byte[] input = this.bout.toByteArray();
            this.deflater.setInput( input );
            this.deflater.finish();

            this.bout.reset();
            byte[] intermediate = new byte[1024];
            while ( !this.deflater.finished() ) {
                int read = this.deflater.deflate( intermediate );
                this.bout.write( intermediate, 0, read );
            }

            return this.bout.toByteArray();
        }

    }

}
