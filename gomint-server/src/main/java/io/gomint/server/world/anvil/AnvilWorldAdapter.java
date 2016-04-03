/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.GoMintServer;
import io.gomint.server.async.Delegate2;
import io.gomint.server.async.Delegate;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBatch;
import io.gomint.server.network.packet.PacketWorldChunk;
import io.gomint.server.world.AsyncChunkLoadTask;
import io.gomint.server.world.AsyncChunkPackageTask;
import io.gomint.server.world.AsyncChunkSaveTask;
import io.gomint.server.world.AsyncChunkTask;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.CoordinateUtils;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTStream;
import io.gomint.taglib.NBTStreamListener;
import io.gomint.world.Block;
import io.gomint.world.Gamerule;

import lombok.Getter;

import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class AnvilWorldAdapter extends WorldAdapter {

    // ==================================== FIELDS ==================================== //

    // Shared objects
    @Getter private final GoMintServer server;
	private final Logger logger;

    // World properties
    private final File worldDir;
    private String levelName;
    private Location spawn;
    private Map<Gamerule, Object> gamerules = new HashMap<>();

    // Chunk Handling
    private AnvilChunkCache chunkCache;

    // I/O
    private int regionXRead;
    private int regionZRead;
    private RegionFile regionFileRead;
    private boolean asyncWorkerRunning;
    private BlockingQueue<AsyncChunkTask> asyncChunkTasks;
    private Queue<AsyncChunkPackageTask> chunkPackageTasks;
    private Deflater deflater;

    // Player handling
    private ObjObjMap<EntityPlayer, ChunkAdapter> players;

    /**
     * Construct and init a new Anvil based World
     *
     * @param server which has requested to load this world
     * @param worldDir the folder where the world should be in
     */
    AnvilWorldAdapter( final GoMintServer server, final File worldDir ) {
        this.server = server;
	    this.logger = LoggerFactory.getLogger( "AnvilWorld-" + worldDir.getName() );
        this.worldDir = worldDir;
        this.chunkCache = new AnvilChunkCache( this );
        this.asyncChunkTasks = new LinkedBlockingQueue<>();
        this.chunkPackageTasks = new ConcurrentLinkedQueue<>();
        this.deflater = new Deflater( Deflater.DEFAULT_COMPRESSION );
        this.players = HashObjObjMaps.newMutableMap();
    }

    // ==================================== WORLD ADAPTER ==================================== //

    @Override
    public String getWorldName() {
        return this.worldDir.getName();
    }

    @Override
    public String getLevelName() {
        return this.levelName;
    }

    @Override
    public Location getSpawnLocation() {
        return this.spawn;
    }

    @Override
    public Block getBlockAt( Vector vector ) {
        return null;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T getGamerule( Gamerule<T> gamerule ) {
        return this.gamerules.containsKey( gamerule ) ? (T) this.gamerules.get( gamerule ) : null;
    }

    @Override
    public void addPlayer( EntityPlayer player ) {
        // Schedule sending spawn region chunks:
        final int minBlockX = (int) (this.spawn.getX() - 64);
        final int minBlockZ = (int) (this.spawn.getZ() - 64);
        final int maxBlockX = (int) (this.spawn.getX() + 64);
        final int maxBlockZ = (int) (this.spawn.getZ() + 64);

        final int minChunkX = CoordinateUtils.fromBlockToChunk( minBlockX );
        final int minChunkZ = CoordinateUtils.fromBlockToChunk( minBlockZ );
        final int maxChunkX = CoordinateUtils.fromBlockToChunk( maxBlockX );
        final int maxChunkZ = CoordinateUtils.fromBlockToChunk( maxBlockZ );

        for ( int i = minChunkZ; i <= maxChunkZ; ++i ) {
            for ( int j = minChunkX; j <= maxChunkX; ++j ) {
                this.sendChunk( j, i, player );
            }
        }
    }

    @Override
    public void removePlayer( EntityPlayer player ) {
        ChunkAdapter chunkAdapter = this.players.remove( player );
        if ( chunkAdapter != null ) {
            chunkAdapter.removePlayer( player );
        }
    }

    @Override
    public void tick( long currentTimeMS ) {
	    // ---------------------------------------
	    // Save all chunks that should be saved to prevent
	    // data loss:


        // ---------------------------------------
        // Tick the chunk cache to get rid of Chunks
        this.chunkCache.tick();

        // ---------------------------------------
        // Chunk packages are done in main thread in order to be able to
        // cache packets without possibly getting into race conditions:
        if ( !this.chunkPackageTasks.isEmpty() ) {
            // One chunk per tick at max:
            AsyncChunkPackageTask task = this.chunkPackageTasks.poll();
            AnvilChunk chunk = (AnvilChunk) this.getChunk( task.getX(), task.getZ() );
            if ( chunk == null ) {
                final Object lock = new Object();

                this.getOrLoadChunk( task.getX(), task.getZ(), false, new Delegate<ChunkAdapter>() {
                    @Override
                    public void invoke( ChunkAdapter arg ) {
                        synchronized ( lock ) {
                            packageChunk( (AnvilChunk) arg, task.getCallback() );
                            lock.notifyAll();
                        }
                    }
                } );

                // Wait until the chunk is loaded
                synchronized ( lock ) {
                    try {
                        lock.wait();
                    } catch ( InterruptedException e ) {
                        // Ignored .-.
                    }
                }
            } else {
                packageChunk( chunk, task.getCallback() );
            }
        }

        // ---------------------------------------
        // Perform regular updates:
    }

    private void packageChunk( AnvilChunk chunk, Delegate2<Long, Packet> callback ) {
        PacketWorldChunk packet = chunk.createPackagedData();

        PacketBuffer buffer = new PacketBuffer( packet.estimateLength() + 1 );
        buffer.writeByte( packet.getId() );
        packet.serialize( buffer );

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        try {
            dout.writeInt( buffer.getPosition() );
            dout.write( buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset() );
            dout.flush();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        this.deflater.reset();
        this.deflater.setInput( bout.toByteArray() );
        this.deflater.finish();

        bout.reset();
        byte[] intermediate = new byte[1024];
        while ( !this.deflater.finished() ) {
            int read = this.deflater.deflate( intermediate );
            bout.write( intermediate, 0, read );
        }

        PacketBatch batch = new PacketBatch();
        batch.setPayload( bout.toByteArray() );

        try {
            dout.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        this.deflater.reset();

        chunk.dirty = false;
        chunk.cachedPacket = new SoftReference<>( batch );
        callback.invoke( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ), batch );
    }

    @Override
    public ChunkAdapter getChunk( int x, int z ) {
        return this.chunkCache.getChunk( x, z );
    }

    @Override
    public void getOrLoadChunk( int x, int z, boolean generate, Delegate<ChunkAdapter> callback ) {
        // Early out:
        AnvilChunk chunk = this.chunkCache.getChunk( x, z );
        if ( chunk != null ) {
            callback.invoke( chunk );
            return;
        }

        // Schedule this chunk for asynchronous loading:
        AsyncChunkLoadTask task = new AsyncChunkLoadTask( x, z, generate, callback );
        this.asyncChunkTasks.offer( task );
    }

    @Override
    public void sendChunk( int x, int z, EntityPlayer player ) {
        Delegate2<Long, Packet> sendDelegate = new Delegate2<Long, Packet>() {
            @Override
            public void invoke( Long chunkHash, Packet chunkPacket ) {
                player.getConnection().sendWorldChunk( chunkHash, chunkPacket );
            }
        };

        this.getOrLoadChunk( x, z, true, new Delegate<ChunkAdapter>() {
            @Override
            public void invoke( ChunkAdapter chunk ) {
                chunk.packageChunk( sendDelegate );
            }
        } );
    }

    @Override
    public void movePlayerToChunk( int x, int z, EntityPlayer player ) {
        ChunkAdapter oldChunk = this.players.get( player );
        getOrLoadChunk( x, z, true, new Delegate<ChunkAdapter>() {
            @Override
            public void invoke( ChunkAdapter newChunk ) {
                if ( oldChunk == null ) {
                    newChunk.addPlayer( player );
                    AnvilWorldAdapter.this.players.put( player, newChunk );
                }

                if ( oldChunk != null && !oldChunk.equals( newChunk ) ) {
                    oldChunk.removePlayer( player );
                    newChunk.addPlayer( player );
                    AnvilWorldAdapter.this.players.put( player, newChunk );
                }
            }
        } );
    }

    // ==================================== INTERNALS ==================================== //

    /**
     * Notifies the world that the given chunk was told to package itself. This will effectively
     * produce an asynchronous chunk task which will be completed by the asynchronous worker thread.
     *
     * @param x        The x coordinate of the chunk we want to package
     * @param z        The z coordinate of the chunk we want to package
     * @param callback The callback to be invoked once the chunk is packaged
     */
    void notifyPackageChunk( int x, int z, Delegate2<Long, Packet> callback ) {
        AsyncChunkPackageTask task = new AsyncChunkPackageTask( x, z, callback );
        this.chunkPackageTasks.add( task );
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

    /**
     * Prepares the region surrounding the world's spawn point.
     *
     * @throws IOException Throws in case the spawn region could not be loaded nor generated
     */
    private void prepareSpawnRegion() throws IOException {
        final int spawnRadius = this.server.getServerConfig().getAmountOfChunksForSpawnArea() * 16;
        if ( spawnRadius == 0 ) return;

        final int minBlockX = (int) (this.spawn.getX() - spawnRadius);
        final int minBlockZ = (int) (this.spawn.getZ() - spawnRadius);
        final int maxBlockX = (int) (this.spawn.getX() + spawnRadius);
        final int maxBlockZ = (int) (this.spawn.getZ() + spawnRadius);

        final int minChunkX = CoordinateUtils.fromBlockToChunk( minBlockX );
        final int minChunkZ = CoordinateUtils.fromBlockToChunk( minBlockZ );
        final int maxChunkX = CoordinateUtils.fromBlockToChunk( maxBlockX );
        final int maxChunkZ = CoordinateUtils.fromBlockToChunk( maxBlockZ );

        for ( int i = minChunkZ; i <= maxChunkZ; ++i ) {
            for ( int j = minChunkX; j <= maxChunkX; ++j ) {
                AnvilChunk chunk = this.loadChunk( j, i, true );
                if ( chunk == null ) {
                    throw new IOException( "Failed to load / generate chunk surrounding spawn region" );
                }
            }
        }
    }

    /**
     * Starts the asynchronous worker thread used by the world to perform I/O operations for chunks.
     */
    private void startAsyncWorker( ExecutorService executorService ) {
        executorService.execute( new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName( Thread.currentThread().getName() + " [Async World I/O: " + AnvilWorldAdapter.this.getWorldName() + "]" );
                AnvilWorldAdapter.this.asyncWorkerLoop();
            }
        } );

        this.asyncWorkerRunning = true;
    }

    /**
     * Main loop of the world's asynchronous worker thread.
     */
    private void asyncWorkerLoop() {
        while ( this.asyncWorkerRunning ) {
            try {
                AsyncChunkTask task = this.asyncChunkTasks.poll( 500, TimeUnit.MILLISECONDS );
                if ( task == null ) continue;

                AnvilChunk chunk;
                switch ( task.getType() ) {
                    case LOAD:
                        AsyncChunkLoadTask load = (AsyncChunkLoadTask) task;
                        chunk = this.loadChunk( load.getX(), load.getZ(), load.isGenerate() );
                        load.getCallback().invoke( chunk );
                        break;

                    case SAVE:
                        // TODO: Implement saving chunks
	                    AsyncChunkSaveTask save = (AsyncChunkSaveTask) task;
	                    chunk = ( (AnvilChunk) save.getChunk() );
                        this.saveChunk( chunk );
                        break;

                    default:
                        // Log some error when this happens

                        break;
                }
            } catch ( Throwable cause ) {
                // Catching throwable in order to make sure no uncaught exceptions puts
                // the asynchronous worker into nirvana:

            }
        }
    }

    // ==================================== I/O ==================================== //

    /**
     * Loads the chunk at the specified coordinates synchronously. If the chunk does not yet exist
     * and generate is set to true it will be generated instead.
     *
     * @param x        The x-coordinate of the chunk to be loaded
     * @param z        The z-coordinate of the chunk to be loaded
     * @param generate Whether or not to generate the chunk if it does not yet exist
     * @return The chunk that was just loaded or null if it could not be loaded
     */
    private AnvilChunk loadChunk( int x, int z, boolean generate ) {
        AnvilChunk chunk = this.chunkCache.getChunk( x, z );
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

	/**
	 * Saves the given chunk to its respective region file. The respective region file
	 * is created automatically if it does not yet exist.
	 *
	 * @param chunk The chunk to be saved
	 */
	private void saveChunk( AnvilChunk chunk ) {
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

			regionFile.saveChunk( chunk, true );
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
        AnvilWorldAdapter world = new AnvilWorldAdapter( server, pathToWorld );
        world.loadLevelDat();
        world.prepareSpawnRegion();
        world.startAsyncWorker( server.getExecutorService() );
        return world;
    }

    /**
     * Get the current view of players on this world.
     *
     * @return The Collection View of the Players currently on this world
     */
    public Map<EntityPlayer,ChunkAdapter> getPlayers() {
        return players;
    }
}