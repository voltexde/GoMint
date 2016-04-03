/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.leveldb;

import io.gomint.math.Location;
import io.gomint.server.GoMintServer;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.ChunkCache;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.anvil.AnvilWorldAdapter;
import io.gomint.taglib.NBTStream;
import io.gomint.taglib.NBTStreamListener;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author geNAZt
 * @version 1.0
 */
public class LevelDBWorldAdapter extends WorldAdapter {

    private DB db;

    /**
     * Construct and init a new levedb based World
     *
     * @param server   which has requested to load this world
     * @param worldDir the folder where the world should be in
     */
    LevelDBWorldAdapter( GoMintServer server, File worldDir ) {
        super( server, worldDir );
        this.chunkCache = new ChunkCache( this );

        // CHECKSTYLE:OFF
        try {
            this.loadLevelDat();
            this.db = Iq80DBFactory.factory.open( new File( this.worldDir, "db" ), new Options().createIfMissing( true ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        // CHECKSTYLE:ON
    }

    private void loadLevelDat() throws Exception {
        File levelDat = new File( this.worldDir, "level.dat" );
        if ( !levelDat.exists() || !levelDat.isFile() ) {
            throw new IOException( "Missing level.dat" );
        }

        // Default the settings
        this.levelName = "";
        this.spawn = new Location( this, 0, 0, 0 );

        try ( FileInputStream stream = new FileInputStream( levelDat ) ) {
            // Skip some data. For example the amount of bytes of this NBT Tag
            stream.skip( 8 );

            NBTStream nbtStream = new NBTStream( stream, ByteOrder.LITTLE_ENDIAN );
            nbtStream.addListener( new NBTStreamListener() {
                @Override
                public void onNBTValue( String path, Object value ) throws Exception {
                    System.out.println( path );
                    switch ( path ) {
                        case ".LevelName":
                            LevelDBWorldAdapter.this.levelName = (String) value;
                            break;
                        case ".SpawnX":
                            LevelDBWorldAdapter.this.spawn.setX( (int) value );
                            break;
                        case ".SpawnY":
                            LevelDBWorldAdapter.this.spawn.setY( (int) value );
                            break;
                        case ".SpawnZ":
                            LevelDBWorldAdapter.this.spawn.setZ( (int) value );
                            break;
                        default:
                            break;
                    }
                }
            } );
            nbtStream.parse();
        }
    }

    @Override
    protected ChunkAdapter loadChunk( int x, int z, boolean generate ) {
        return null;
    }

    @Override
    protected void saveChunk( ChunkAdapter chunk ) {

    }

    /**
     * Loads an leveldb world given the path to the world's directory. This operation
     * performs synchronously and will at least load the entire spawn region before
     * completing.
     *
     * @param server      The GoMint Server which runs this
     * @param pathToWorld The path to the world's directory
     * @return The leveldb world adapter used to access the world
     * @throws Exception Thrown in case the world could not be loaded successfully
     */
    public static LevelDBWorldAdapter load( GoMintServer server, File pathToWorld ) throws Exception {
        return new LevelDBWorldAdapter( server, pathToWorld );
    }

}
