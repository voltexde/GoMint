package io.gomint.server.network;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Deflater;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PostProcessWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger( PostProcessWorker.class );

    private AtomicBoolean running = new AtomicBoolean( true );
    @Getter private BlockingQueue<Packet[]> queuedPacketBatches = new LinkedBlockingQueue<>();
    private final BatchStreamHolder batchHolder = new BatchStreamHolder();
    private final PlayerConnection connection;
    @Setter private EncryptionHandler encryptionHandler;

    @Override
    public void run() {
        Thread.currentThread().setName( "Connection-PostProcessor#" + this.connection.getConnection().getGuid() );

        // This runnable does zip and encrypt data and send it to the raknet connection
        while ( running.get() ) {
            try {
                Packet[] packetBatch = this.queuedPacketBatches.poll( 50, TimeUnit.MILLISECONDS );
                if ( packetBatch != null ) {
                    // Batch them first
                    for ( Packet packet : packetBatch ) {
                        // LOGGER.debug( "Sending packet: " + packet );

                        PacketBuffer buffer = new PacketBuffer( 64 );
                        buffer.writeByte( packet.getId() );
                        buffer.writeShort( (short) 0 );
                        packet.serialize( buffer );

                        try {
                            writeVarInt( buffer.getPosition(), this.batchHolder.getOutputStream() );
                            this.batchHolder.getOutputStream().write( buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset() );
                        } catch ( IOException e ) {
                            e.printStackTrace();
                        }
                    }

                    PacketBatch batch = new PacketBatch();
                    batch.setPayload( this.batchHolder.getBytes() );

                    if ( this.encryptionHandler != null ) {
                        batch.setPayload( this.encryptionHandler.encryptInputForClient( batch.getPayload() ) );
                    }

                    this.batchHolder.reset();
                    this.connection.send( batch );
                }
            } catch ( InterruptedException e ) {
                // Ignored
            }
        }
    }

    private void writeVarInt( int value, OutputStream stream ) throws IOException {
        int copyValue = value;

        while ( ( copyValue & -128 ) != 0 ) {
            stream.write( copyValue & 127 | 128 );
            copyValue >>>= 7;
        }

        stream.write( copyValue );
    }

    /**
     * Close this worker and let it stop working
     */
    public void close() {
        this.running.set( false );
    }

    private final class BatchStreamHolder {

        private ByteArrayOutputStream bout;
        private final Deflater deflater;

        private BatchStreamHolder() {
            this.bout = new ByteArrayOutputStream();
            this.deflater = new Deflater();
        }

        private void reset() {
            this.bout = new ByteArrayOutputStream();
            this.deflater.reset();
            this.deflater.setInput( new byte[0] );
        }

        private OutputStream getOutputStream() {
            return this.bout;
        }

        private byte[] getBytes() {
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
