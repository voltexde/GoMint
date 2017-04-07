package io.gomint.server.util;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBatch;

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

    private static final ObjectBuffer<BatchStreamHolder> STREAM_BUFFER = new ObjectBuffer<>( 5 );
    private static long lastChecked = System.currentTimeMillis();

    /**
     * Batch multiple packets together and zlib compress them
     *
     * @param packets The packets which should be batched
     * @return the completly ready to be sent batch packet
     */
    public static PacketBatch batch( Collection<Packet> packets ) {
        if ( System.currentTimeMillis() - lastChecked >= TimeUnit.SECONDS.toMillis( 10 ) ) {
            STREAM_BUFFER.recalc();
        }

        BatchStreamHolder batchStreamHolder = STREAM_BUFFER.get();
        if ( batchStreamHolder == null ) {
            batchStreamHolder = new BatchStreamHolder();
        }

        for ( Packet packet : packets ) {
            PacketBuffer buffer = new PacketBuffer( packet.estimateLength() == -1 ? 64 : packet.estimateLength() );
            buffer.writeByte( packet.getId() );
            packet.serialize( buffer );

            try {
                ByteUtil.writeVarInt( buffer.getPosition(), batchStreamHolder.getOutputStream() );
                batchStreamHolder.getOutputStream().write( buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset() );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        PacketBatch batch = new PacketBatch();
        batch.setPayload( batchStreamHolder.getBytes() );

        batchStreamHolder.reset();
        STREAM_BUFFER.push( batchStreamHolder );

        return batch;
    }

    public static PacketBatch batch( Packet... packets ) {
        return batch( Arrays.asList( packets ) );
    }

}
