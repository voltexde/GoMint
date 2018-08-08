package io.gomint.server.network;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.jni.NativeCode;
import io.gomint.server.jni.zlib.JavaZLib;
import io.gomint.server.jni.zlib.NativeZLib;
import io.gomint.server.jni.zlib.ZLib;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBatch;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.zip.Adler32;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PostProcessWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger( PostProcessWorker.class );
    private static final NativeCode<ZLib> ZLIB = new NativeCode<>( "zlib", JavaZLib.class, NativeZLib.class );
    private static final ThreadLocal<ZLib> COMPRESSOR = new ThreadLocal<>();

    static {
        ZLIB.load();
    }

    private final PlayerConnection connection;
    private final List<Packet> packets;

    public PostProcessWorker( PlayerConnection connection, List<Packet> packets ) {
        this.connection = connection;
        this.packets = packets;
    }

    private ZLib getCompressor() {
        if ( COMPRESSOR.get() == null ) {
            ZLib zLib = ZLIB.newInstance();
            zLib.init( true, this.connection.getServer().getServerConfig().getConnection().getCompressionLevel() );
            COMPRESSOR.set( zLib );
            return zLib;
        }

        return COMPRESSOR.get();
    }

    @Override
    public void run() {
        ByteBuf inBuf = PooledByteBufAllocator.DEFAULT.directBuffer();

        // Write all packets into the inBuf for compression
        PacketBuffer buffer = new PacketBuffer( 64 );

        for ( Packet packet : this.packets ) {
            buffer.setPosition( 0 );

            packet.serializeHeader( buffer, this.connection.getConnection().getProtocolVersion() );
            packet.serialize( buffer, this.connection.getProtocolID() );

            writeVarInt( buffer.getPosition(), inBuf );
            inBuf.writeBytes( buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset() );
        }

        int inBytes = inBuf.readableBytes();
        LOGGER.debug( "Compressing {} bytes", inBytes );

        ByteBuf outBuf = null;
        byte[] data = null;

        try {
            if ( inBytes > 256 ) {
                ZLib compressor = this.getCompressor();
                outBuf = PooledByteBufAllocator.DEFAULT.directBuffer( 8192 );
                compressor.process( inBuf, outBuf );
            } else {
                inBuf.readerIndex( 0 );
                data = new byte[inBytes + 7 + 4];
                data[0] = 0x78;
                data[1] = 0x01;
                data[2] = 0x01;

                // Write data length
                int length = inBytes;
                data[3] = (byte) length;
                data[4] = (byte) ( length >>> 8 );
                length = ~length;
                data[5] = (byte) length;
                data[6] = (byte) ( length >>> 8 );

                // Write data
                inBuf.readBytes( data, 7, inBuf.readableBytes() );

                long checksum = adler32( data, 7, data.length - 11 );
                data[data.length - 4] = ( (byte) ( ( checksum >> 24 ) % 256 ) );
                data[data.length - 3] = ( (byte) ( ( checksum >> 16 ) % 256 ) );
                data[data.length - 2] = ( (byte) ( ( checksum >> 8 ) % 256 ) );
                data[data.length - 1] = ( (byte) ( checksum % 256 ) );
            }
        } catch ( Exception e ) {
            LOGGER.error( "Could not compress data for network", e );
            if ( outBuf != null ) outBuf.release();
            return;
        } finally {
            inBuf.release();
        }

        if ( outBuf != null ) {
            data = new byte[outBuf.readableBytes()];
            outBuf.readBytes( data );
            outBuf.release();
        }

        PacketBatch batch = new PacketBatch();
        batch.setPayload( data );

        EncryptionHandler encryptionHandler = this.connection.getEncryptionHandler();
        if ( encryptionHandler != null && ( this.connection.getState() == PlayerConnectionState.LOGIN || this.connection.getState() == PlayerConnectionState.PLAYING ) ) {
            batch.setPayload( encryptionHandler.encryptInputForClient( batch.getPayload() ) );
        }

        this.connection.send( batch );
    }

    /**
     * Calculates the adler32 checksum of the data
     */
    private static long adler32( byte[] data, int offset, int length ) {
        final Adler32 checksum = new Adler32();
        checksum.update( data, offset, length );
        return checksum.getValue();
    }

    private void writeVarInt( int value, ByteBuf stream ) {
        int copyValue = value;

        while ( ( copyValue & -128 ) != 0 ) {
            stream.writeByte( copyValue & 127 | 128 );
            copyValue >>>= 7;
        }

        stream.writeByte( copyValue );
    }

}
