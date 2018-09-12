package io.gomint.server.world.converter;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.leveldb.DB;
import io.gomint.leveldb.NativeLoader;
import io.gomint.leveldb.WriteBatch;
import io.gomint.math.MathUtils;
import io.gomint.server.util.BlockIdentifier;
import io.gomint.server.util.Palette;
import io.gomint.taglib.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BaseConverter {

    static {
        NativeLoader.load();
    }

    protected File worldFolder;

    private DB db;
    private ThreadLocal<WriteBatch> writeBatch = new ThreadLocal<>();
    private ThreadLocal<PacketBuffer> buffer = new ThreadLocal<>();

    public BaseConverter( File worldFolder ) {
        this.worldFolder = worldFolder;

        // Create new leveldb
        this.db = new DB( new File( worldFolder, "db" ) );
        this.db.open();
    }

    private ByteBuf getKey( int chunkX, int chunkZ, byte dataType ) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer( 9 );
        return buf.writeInt( chunkX ).writeInt( chunkZ ).writeByte( dataType );

        /*return new byte[]{
            (byte) ( chunkX & 0xFF ),
            (byte) ( ( chunkX >>> 8 ) & 0xFF ),
            (byte) ( ( chunkX >>> 16 ) & 0xFF ),
            (byte) ( ( chunkX >>> 24 ) & 0xFF ),
            (byte) ( chunkZ & 0xFF ),
            (byte) ( ( chunkZ >>> 8 ) & 0xFF ),
            (byte) ( ( chunkZ >>> 16 ) & 0xFF ),
            (byte) ( ( chunkZ >>> 24 ) & 0xFF ),
            dataType
        };*/
    }

    private ByteBuf getKeySubChunk( int chunkX, int chunkZ, byte dataType, byte subChunk ) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer( 10 );
        return buf.writeInt( chunkX ).writeInt( chunkZ ).writeByte( dataType ).writeByte( subChunk );

        /*return new byte[]{
            (byte) ( chunkX & 0xFF ),
            (byte) ( ( chunkX >>> 8 ) & 0xFF ),
            (byte) ( ( chunkX >>> 16 ) & 0xFF ),
            (byte) ( ( chunkX >>> 24 ) & 0xFF ),
            (byte) ( chunkZ & 0xFF ),
            (byte) ( ( chunkZ >>> 8 ) & 0xFF ),
            (byte) ( ( chunkZ >>> 16 ) & 0xFF ),
            (byte) ( ( chunkZ >>> 24 ) & 0xFF ),
            dataType,
            subChunk
        };*/
    }

    private PacketBuffer getBuffer() {
        PacketBuffer buffer = this.buffer.get();
        if ( buffer == null ) {
            buffer = new PacketBuffer( 2048 );
            this.buffer.set( buffer );
        }

        return buffer;
    }

    private WriteBatch getWriteBatch() {
        WriteBatch batch = this.writeBatch.get();
        if ( batch == null ) {
            batch = new WriteBatch();
            this.writeBatch.set( batch );
        }

        return batch;
    }

    protected void startChunk( int chunkX, int chunkZ ) {
        WriteBatch batch = this.getWriteBatch();

        ByteBuf keyA = this.getKey( chunkX, chunkZ, (byte) 0x76 );
        ByteBuf keyB = this.getKey( chunkX, chunkZ, (byte) 0x36 );

        ByteBuf valA = allocate( new byte[]{ 7 } );
        ByteBuf valB = allocate( new byte[]{ 2, 0, 0, 0 } );

        batch.put( keyA, valA );
        batch.put( keyB, valB );

        keyA.release();
        keyB.release();
        valA.release();
        valB.release();
    }

    protected void storeSubChunkBlocks( int sectionY, int chunkX, int chunkZ, BlockIdentifier[] blocks ) {
        // Count how many unique blocks we have in this chunk
        int[] indexIDs = new int[4096];

        LongList indexList = new LongArrayList();
        IntList runtimeIndex = new IntArrayList();
        Int2ObjectMap<BlockIdentifier> block = new Int2ObjectOpenHashMap<>();

        int foundIndex = 0;

        String lastBlockId = "";
        short lastDataId = -1;

        int runtimeIdCounter = 0;

        for ( short blockIndex = 0; blockIndex < indexIDs.length; blockIndex++ ) {
            String blockId = blocks[blockIndex].getBlockId();
            short blockData = blocks[blockIndex].getData();

            if ( !blockId.equals( lastBlockId ) || blockData != lastDataId ) {
                long hashId = ( (long) blockId.hashCode() ) << 32 | ( blockData & 0xFF );

                foundIndex = indexList.indexOf( hashId );
                if ( foundIndex == -1 ) {
                    int runtimeId = runtimeIdCounter++;
                    block.put( runtimeId, blocks[blockIndex] );
                    runtimeIndex.add( runtimeId );
                    indexList.add( hashId );
                    foundIndex = indexList.size() - 1;
                }

                lastBlockId = blockId;
                lastDataId = blockData;
            }

            indexIDs[blockIndex] = foundIndex;
        }

        // Get correct wordsize
        int value = indexList.size();
        int numberOfBits = MathUtils.fastFloor( log2( value ) ) + 1;

        // Prepare palette
        int amountOfBlocks = MathUtils.fastFloor( 32f / (float) numberOfBits );

        PacketBuffer buffer = this.getBuffer();
        Palette palette = new Palette( buffer, amountOfBlocks, false );

        byte paletteWord = (byte) ( (byte) ( palette.getPaletteVersion().getVersionId() << 1 ) | 1 );
        buffer.writeByte( paletteWord );
        palette.addIndexIDs( indexIDs );
        palette.finish();

        // Write persistant ids
        buffer.writeLInt( indexList.size() );
        for ( int value1 : runtimeIndex.toArray( new int[0] ) ) {
            BlockIdentifier blockIdentifier = block.get( value1 );

            NBTTagCompound compound = new NBTTagCompound( "" );
            compound.addValue( "name", blockIdentifier.getBlockId() );
            compound.addValue( "val", blockIdentifier.getData() );

            try {
                compound.writeTo( new OutputStream() {
                    @Override
                    public void write( byte[] b, int off, int len ) throws IOException {
                        byte[] finalBytes = new byte[len];
                        System.arraycopy( b, off, finalBytes, 0, len );
                        buffer.writeBytes( finalBytes );
                    }

                    @Override
                    public void write( int b ) throws IOException {
                        buffer.writeByte( (byte) b );
                    }
                }, false, ByteOrder.LITTLE_ENDIAN );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        ByteBuf key = this.getKeySubChunk( chunkX, chunkZ, (byte) 0x27, (byte) sectionY );
        ByteBuf val = allocate( Arrays.copyOf( buffer.getBuffer(), buffer.getPosition() ) );

        this.getWriteBatch().put( key, val );

        key.release();
        val.release();

        buffer.setPosition( 0 );
    }

    private ByteBuf allocate( byte[] data ) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer( data.length );
        buf.writeBytes( data );
        return buf;
    }

    private int log2( int n ) {
        if ( n <= 0 ) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros( n );
    }

    public void done() {
        this.db.close();
    }

    protected void finish() {
        WriteBatch batch = this.getWriteBatch();
        if ( batch.size() > 0 ) {
            this.db.write( batch );
            batch.clear();
        }

        batch.close();
        this.writeBatch.remove();
    }

    protected void persistChunk() {
        // Check for size
        WriteBatch batch = this.getWriteBatch();
        if ( batch.size() > 16 * 1024 * 1024 ) {    // We persist every 16 MB
            this.db.write( batch );
            batch.clear();
        }
    }

}
