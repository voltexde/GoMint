package io.gomint.server.world.converter.anvil;

import io.gomint.server.assets.AssetsLibrary;
import io.gomint.server.util.BlockIdentifier;
import io.gomint.server.util.Pair;
import io.gomint.server.world.NibbleArray;
import io.gomint.server.world.converter.BaseConverter;
import io.gomint.taglib.NBTTagCompound;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class AnvilConverter extends BaseConverter {

    private BlockConverter converter;

    public AnvilConverter( AssetsLibrary assets, File worldFolder ) {
        super( worldFolder );

        // Setup block converter
        this.converter = new BlockConverter( assets.getConverterData() );

        // Convert all region files first
        File regionFolder = new File( worldFolder, "region" );
        if ( regionFolder.exists() ) {
            convertRegionFiles( regionFolder );
        }
    }

    private void convertRegionFiles( File regionFolder ) {
        File[] regionFiles = regionFolder.listFiles( ( dir, name ) -> name.endsWith( ".mca" ) );
        if ( regionFiles == null ) {
            return;
        }

        int amountOfChunksDone = 0;

        // Iterate over all region files and check if they match the pattern
        long start = System.nanoTime();
        for ( File regionFile : regionFiles ) {
            String fileName = regionFile.getName();
            if ( fileName.startsWith( "r." ) ) {
                String[] split = fileName.split( "\\." );
                if ( split.length != 4 ) {
                    continue;
                }

                try {
                    RegionFileSingleChunk regionFileReader = new RegionFileSingleChunk( regionFile );

                    for ( int x = 0; x < 32; x++ ) {
                        for ( int z = 0; z < 32; z++ ) {
                            NBTTagCompound compound = regionFileReader.loadChunk( x, z );
                            if ( compound == null ) {
                                continue;
                            }

                            NBTTagCompound levelCompound = compound.getCompound( "Level", false );

                            int chunkX = levelCompound.getInteger( "xPos", 0 );
                            int chunkZ = levelCompound.getInteger( "zPos", 0 );

                            this.startChunk( chunkX, chunkZ );

                            List<Object> sections = levelCompound.getList( "Sections", false );
                            for ( Object section : sections ) {
                                NBTTagCompound sectionCompound = (NBTTagCompound) section;
                                this.readAndConvertSubchunk( chunkX, chunkZ, sectionCompound );
                            }

                            List<Object> tileEntiies = levelCompound.getList( "TileEntities", false );
                            if ( tileEntiies != null && tileEntiies.size() > 0 ) {
                                // System.out.println( "Found tiles" );
                            }

                            amountOfChunksDone++;

                            this.persistChunk();
                        }
                    }
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        // Make a level.dat
        try {
            NBTTagCompound levelDat = NBTTagCompound.readFrom( new File( this.worldFolder, "level.dat" ), true, ByteOrder.BIG_ENDIAN );
            NBTTagCompound dataCompound = levelDat.getCompound( "Data", false );
            System.out.println( dataCompound );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        System.out.println( "Done in " + TimeUnit.NANOSECONDS.toMillis( System.nanoTime() - start ) + " ms - Processed " + amountOfChunksDone + " chunks" );
    }

    private void readAndConvertSubchunk( int chunkX, int chunkZ, NBTTagCompound section ) {
        byte[] blocks = section.getByteArray( "Blocks", new byte[0] );
        byte[] addBlocks = section.getByteArray( "Add", new byte[0] );
        int sectionY = section.getByte( "Y", (byte) 0 );

        NibbleArray add = addBlocks.length > 0 ? NibbleArray.create( addBlocks ) : null;
        NibbleArray data = NibbleArray.create( section.getByteArray( "Data", new byte[0] ) );

        if ( blocks == null ) {
            throw new IllegalArgumentException( "Corrupt chunk: Section is missing obligatory compounds" );
        }

        BlockIdentifier[] newBlocks = new BlockIdentifier[4096];

        for ( int j = 0; j < 16; ++j ) {
            for ( int i = 0; i < 16; ++i ) {
                for ( int k = 0; k < 16; ++k ) {
                    int blockIndex = ( j << 8 | k << 4 | i );

                    int blockId = ( ( ( add != null ? add.get( blockIndex ) << 8 : 0 ) | blocks[blockIndex] ) & 0xFF );
                    byte blockData = data.get( blockIndex );

                    // Block data converter
                    if ( blockId == 3 && blockData == 1 ) {
                        blockId = 198;
                        blockData = 0;
                    } else if ( blockId == 3 && blockData == 2 ) {
                        blockId = 243;
                        blockData = 0;
                    }

                    // Fix water & lava at the bottom of a chunk
                    if ( sectionY + j == 0 && ( blockId == 8 || blockId == 9 || blockId == 10 || blockId == 11 ) ) {
                        blockId = 7;
                        blockData = 0;
                    }

                    short newIndex = (short) ( ( i << 8 ) + ( k << 4 ) + j );
                    Pair<String, Byte> converted = this.converter.convert( (short) blockId, blockData );
                    if ( converted == null ) {
                        newBlocks[newIndex] = new BlockIdentifier( "minecraft:air", (short) 0 );
                        System.out.println( "Invalid block? " + blockId + ":" + blockData );
                    } else {
                        newBlocks[newIndex] = new BlockIdentifier( converted.getFirst(), converted.getSecond() );
                    }
                }
            }
        }

        this.storeSubChunkBlocks( sectionY, chunkX, chunkZ, newBlocks );
    }

}
