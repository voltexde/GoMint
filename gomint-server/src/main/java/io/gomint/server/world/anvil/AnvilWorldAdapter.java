/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.math.Location;
import io.gomint.server.GoMintServer;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.ChunkCache;
import io.gomint.server.world.CoordinateUtils;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTStream;
import io.gomint.taglib.NBTStreamListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class AnvilWorldAdapter extends WorldAdapter {

    // ==================================== FIELDS ==================================== //

    // I/O
    private int regionXRead;
    private int regionZRead;
    private RegionFile regionFileRead;

    /**
     * Construct and init a new Anvil based World
     *
     * @param server which has requested to load this world
     * @param worldDir the folder where the world should be in
     */
    AnvilWorldAdapter( final GoMintServer server, final File worldDir ) {
		super( server, worldDir );
        this.chunkCache = new ChunkCache( this );

        // Load this world
        // CHECKSTYLE:OFF
        try {
            this.loadLevelDat();
            this.prepareSpawnRegion();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        // CHECKSTYLE:ON
    }

    /**
     * Loads all information about the world given inside the level.dat file found
     * in the world's root directory.
     *
     * @throws IOException Thrown in case the level.dat file could not be loaded
     */
    private void loadLevelDat() throws Exception {
        try {
            File levelDat = new File( this.worldDir, "level.dat" );
            if ( !levelDat.exists() || !levelDat.isFile() ) {
                throw new IOException( "Missing level.dat" );
            }

            // Default the settings
            this.levelName = "";
            this.spawn = new Location( this, 0, 0, 0 );

            // Stream the contents to save memory usage
            try ( InputStream in = new BufferedInputStream( new GZIPInputStream( new FileInputStream( levelDat ) ) ) ) {
                NBTStream nbtStream = new NBTStream(
                        in,
                        ByteOrder.BIG_ENDIAN
                );

                nbtStream.addListener( new NBTStreamListener() {
                    @Override
                    public void onNBTValue( String path, Object value ) throws Exception {
                        switch ( path ) {
                            case ".Data.version":
                                if ( (int) value != 19133 ) {
                                    throw new IOException( "unsupported world format" );
                                }
                                break;
                            case ".Data.LevelName":
                                AnvilWorldAdapter.this.levelName = (String) value;
                                break;
                            case ".Data.SpawnX":
                                AnvilWorldAdapter.this.spawn.setX( (int) value );
                                break;
                            case ".Data.SpawnY":
                                AnvilWorldAdapter.this.spawn.setY( (int) value );
                                break;
                            case ".Data.SpawnZ":
                                AnvilWorldAdapter.this.spawn.setZ( (int) value );
                                break;
                            default:
                                break;
                        }
                    }
                } );

                nbtStream.parse();
            }
        } catch ( IOException e ) {
            throw new IOException( "Failed to load anvil world: " + e.getMessage() );
        }
    }

    @Override
    protected ChunkAdapter loadChunk( int x, int z, boolean generate ) {
        ChunkAdapter chunk = this.chunkCache.getChunk( x, z );
        if ( chunk == null ) {
            try {
                int regionX = CoordinateUtils.fromChunkToRegion( x );
                int regionZ = CoordinateUtils.fromChunkToRegion( z );

                RegionFile regionFile = null;
                if ( this.regionFileRead != null && this.regionXRead == regionX && this.regionZRead == regionZ ) {
                    regionFile = this.regionFileRead;
                }

                if ( regionFile == null ) {
                    this.regionFileRead = new RegionFile( this, new File( this.worldDir, String.format( "region%sr.%d.%d.mca", File.separator, regionX, regionZ ) ) );
                    this.regionXRead = regionX;
                    this.regionZRead = regionZ;
                    regionFile = this.regionFileRead;
                }

                chunk = regionFile.loadChunk( x, z );
                if ( chunk != null ) {
                    this.chunkCache.putChunk( chunk );
                } else if ( generate ) {
                    // TODO: Implement chunk generation here
                }

                return chunk;
            } catch ( IOException e ) {
                if ( generate ) {
                    // TODO: Implement chunk generation here
                }

                return null;
            }
        }

        return chunk;
    }

    @Override
    protected void saveChunk( ChunkAdapter chunk ) {
		if ( chunk == null ) {
			return;
		}

		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();
		int regionX = CoordinateUtils.fromChunkToRegion( chunkX );
		int regionZ = CoordinateUtils.fromChunkToRegion( chunkZ );

		try {
			RegionFile regionFile = null;
			if ( this.regionFileRead != null && this.regionXRead == regionX && this.regionZRead == regionZ ) {
				regionFile = this.regionFileRead;
			}

			if ( regionFile == null ) {
				this.regionFileRead = new RegionFile( this, new File( this.worldDir, String.format( "region%sr.%d.%d.mca", File.separator, regionX, regionZ ) ) );
				this.regionXRead = regionX;
				this.regionZRead = regionZ;
				regionFile = this.regionFileRead;
			}

			regionFile.saveChunk( (AnvilChunk) chunk, true );
		} catch ( IOException e ) {
			this.logger.error( "Failed to save chunk to region file", e );
		}
	}

    /**
     * Loads an anvil world given the path to the world's directory. This operation
     * performs synchronously and will at least load the entire spawn region before
     * completing.
     *
     * @param server The GoMint Server which runs this
     * @param pathToWorld The path to the world's directory
     * @return The anvil world adapter used to access the world
     * @throws Exception Thrown in case the world could not be loaded successfully
     */
    public static AnvilWorldAdapter load( GoMintServer server, File pathToWorld ) throws Exception {
        return new AnvilWorldAdapter( server, pathToWorld );
    }

}