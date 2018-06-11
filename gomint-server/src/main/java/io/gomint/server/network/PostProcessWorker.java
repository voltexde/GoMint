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

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PostProcessWorker implements Runnable {

    private static final int ADLER_BASE = 65521;
    private static final Logger LOGGER = LoggerFactory.getLogger( PostProcessWorker.class );
    private static final NativeCode<ZLib> ZLIB = new NativeCode<>( "zlib", JavaZLib.class, NativeZLib.class );
    private static final ThreadLocal<ZLib> COMPRESSOR = new ThreadLocal<>();

    static {
        ZLIB.load();
    }

    private final PlayerConnection connection;
    private final Packet[] packets;

    public PostProcessWorker( PlayerConnection connection, Packet[] packets ) {
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
        this.connection.getServer().getWatchdog().add( 1, TimeUnit.SECONDS );

        ByteBuf inBuf = PooledByteBufAllocator.DEFAULT.directBuffer();

        // Write all packets into the inBuf for compression
        for ( Packet packet : this.packets ) {
            PacketBuffer buffer = new PacketBuffer( 64 );
            buffer.writeByte( packet.getId() );
            buffer.writeShort( (short) 0 );
            packet.serialize( buffer, this.connection.getProtocolID() );

            LOGGER.debug( "Writing packet {} to client: {}", Integer.toHexString( packet.getId() & 0xFF ), packet );

            writeVarInt( buffer.getPosition(), inBuf );
            inBuf.writeBytes( buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset() );
        }

        LOGGER.debug( "Compressing {} bytes, threshold is {}", inBuf.readableBytes(), this.connection.getServer().getServerConfig().getConnection().getCompressionThreshold() );

        ZLib compressor = this.getCompressor();
        ByteBuf outBuf = null;
        byte[] data = null;

        try {
            // Only compress when useful (> threshold)
            if ( inBuf.readableBytes() > this.connection.getServer().getServerConfig().getConnection().getCompressionThreshold() ) {
                outBuf = PooledByteBufAllocator.DEFAULT.directBuffer( 8192 ); // We will write at least once so ensureWrite will realloc to 8192 so or so
                compressor.process( inBuf, outBuf );
            } else {
                data = new byte[inBuf.readableBytes() + 7 + 4];
                data[0] = 0x78;
                data[1] = 0x01;
                data[2] = 0x01;

                // Write data length
                int length = inBuf.readableBytes();
                data[3] = (byte) length;
                data[4] = (byte) ( length >>> 8 );
                length = ~length;
                data[5] = (byte) length;
                data[6] = (byte) ( length >>> 8 );

                // Write data
                inBuf.readBytes( data, 7, inBuf.readableBytes() );

                int adler = adler( data, 7, data.length - 11 );
                data[data.length - 4] = (byte)(adler >>> 24);
                data[data.length - 3] = (byte)(adler >>> 16);
                data[data.length - 2] = (byte)(adler >>> 8);
                data[data.length - 1] = (byte)adler;                                                                     // Write adler32 value
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

        this.connection.getServer().getWatchdog().done();
        this.connection.send( batch );
    }

    private void writeVarInt( int value, ByteBuf stream ) {
        int copyValue = value;

        while ( ( copyValue & -128 ) != 0 ) {
            stream.writeByte( copyValue & 127 | 128 );
            copyValue >>>= 7;
        }

        stream.writeByte( copyValue );
    }

    private long chop( long a ) {
        long tmp = a >> 16;
        a &= 0xffffL;
        a += ( tmp << 4 ) - tmp;
        return a;
    }

    private long mod28( long a ) {
        a = chop( a );
        if ( a >= ADLER_BASE ) {
            a -= ADLER_BASE;
        }

        return a;
    }

    private int adler( byte[] data, int offset, int length ) {
        int s1 = 1;
        int s2 = 0;

        // Fast out
        if ( length == 1 ) {
            s1 += data[offset];
            s2 += s1;
            return s2 << 16 | s1;
        }

        // Use faster modulo
        if ( length < 16 ) {
            for ( int i = 0; i < length; i++ ) {
                s1 += data[offset + i];
                s2 += s1;
            }

            if ( s1 >= ADLER_BASE ) {
                s1 -= ADLER_BASE;
            }

            s2 = (int) mod28( s2 );
            return s2 << 16 | s1;
        }

        short counter = 0;

        for ( int i = 0; i < length; i++ ) {
            s1 += data[offset + i];
            s2 += s1;

            counter++;
            if ( counter == 5552 ) {
                s1 %= ADLER_BASE;
                s2 %= ADLER_BASE;
                counter = 0;
            }
        }

        if ( counter > 0 ) {
            s1 %= ADLER_BASE;
            s2 %= ADLER_BASE;
        }

        return s2 << 16 | s1;
    }

}
