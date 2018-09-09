package io.gomint.server.world.converter;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.leveldb.DB;
import io.gomint.leveldb.NativeLoader;
import io.gomint.leveldb.WriteBatch;
import io.gomint.math.MathUtils;
import io.gomint.server.util.BlockIdentifier;
import io.gomint.server.util.Palette;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
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
    private WriteBatch writeBatch;

    public BaseConverter( File worldFolder ) {
        this.worldFolder = worldFolder;

        // Create new leveldb
        this.db = new DB( new File( worldFolder, "db" ) );
        this.db.open();
    }

    private byte[] getKey( int chunkX, int chunkZ, byte dataType ) {
        return new byte[]{
            (byte) ( chunkX & 0xFF ),
            (byte) ( ( chunkX >>> 8 ) & 0xFF ),
            (byte) ( ( chunkX >>> 16 ) & 0xFF ),
            (byte) ( ( chunkX >>> 24 ) & 0xFF ),
            (byte) ( chunkZ & 0xFF ),
            (byte) ( ( chunkZ >>> 8 ) & 0xFF ),
            (byte) ( ( chunkZ >>> 16 ) & 0xFF ),
            (byte) ( ( chunkZ >>> 24 ) & 0xFF ),
            dataType
        };
    }

    private byte[] getKeySubChunk( int chunkX, int chunkZ, byte dataType, byte subChunk ) {
        return new byte[]{
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
        };
    }

    protected void startChunk( int chunkX, int chunkZ ) {
        this.writeBatch = new WriteBatch();
        this.writeBatch.put( ByteBuffer.wrap( this.getKey( chunkX, chunkZ, (byte) 0x76 ) ), ByteBuffer.wrap( new byte[]{ 7 } ) );
        this.writeBatch.put( ByteBuffer.wrap( this.getKey( chunkX, chunkZ, (byte) 0x36 ) ), ByteBuffer.wrap( new byte[]{ 2, 0, 0, 0 } ) );
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

        PacketBuffer buffer = new PacketBuffer( 16 );
        Palette palette = new Palette( buffer, amountOfBlocks, false );

        byte paletteWord = (byte) ( (byte) ( palette.getPaletteVersion().getVersionId() << 1 ) | 1 );
        buffer.writeByte( paletteWord );
        palette.addIndexIDs( indexIDs );
        palette.finish();

        // Write persistant ids
        buffer.writeLInt( indexList.size() );
        runtimeIndex.forEach( (IntConsumer) value1 -> {
            BlockIdentifier blockIdentifier = block.get( value1 );
            if ( blockIdentifier == null ) {
                System.out.println( "WTF HAPPEND?" );
            }

            NBTTagCompound compound = new NBTTagCompound( "" );
            compound.addValue( "name", blockIdentifier.getBlockId() );
            compound.addValue( "val", blockIdentifier.getData() );

            try {
                compound.writeTo( new OutputStream() {
                    @Override
                    public void write( int b ) throws IOException {
                        buffer.writeByte( (byte) b );
                    }
                }, false, ByteOrder.LITTLE_ENDIAN );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } );

        this.writeBatch.put( ByteBuffer.wrap( this.getKeySubChunk( chunkX, chunkZ, (byte) 0x27, (byte) sectionY ) ), ByteBuffer.wrap( Arrays.copyOf( buffer.getBuffer(), buffer.getPosition() ) ) );
    }

    private int log2( int n ) {
        if ( n <= 0 ) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros( n );
    }

    public void done() {
        this.db.close();
    }

    protected void persistChunk() {
        this.db.write( this.writeBatch );
        this.writeBatch.clear();
        this.writeBatch = null;
    }

}
