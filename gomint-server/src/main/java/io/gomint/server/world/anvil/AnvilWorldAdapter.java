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
import io.gomint.server.async.TwoArgDelegate;
import io.gomint.server.async.Delegate;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBatch;
import io.gomint.server.network.packet.PacketWorldChunk;
import io.gomint.server.world.AsyncChunkLoadTask;
import io.gomint.server.world.AsyncChunkPackageTask;
import io.gomint.server.world.AsyncChunkTask;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.CoordinateUtils;
import io.gomint.server.world.Weather;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.WorldBorder;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.Block;
import io.gomint.world.GameDifficulty;
import io.gomint.world.Gamerule;
import lombok.Getter;
import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.Deflater;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class AnvilWorldAdapter extends WorldAdapter {

    private static final Logger logger = LoggerFactory.getLogger( WorldAdapter.class );

    // ==================================== FIELDS ==================================== //

    // Shared objects
    private final Random random = new Random();
    @Getter
    private final GoMintServer server;

    // World properties
    private final File worldDir;
    private String levelName;
    private long randomSeed;
    private boolean mapFeatures;
    private long lastPlayed;
    private boolean allowCommands;
    private boolean hardcore;
    private GameDifficulty difficulty;
    private boolean difficultyLocked;
    private long time;
    private long dayTime;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private WorldBorder worldBorder;
    private Weather weather;
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
        this.worldDir = worldDir;
        this.chunkCache = new AnvilChunkCache( this );
        this.asyncChunkTasks = new LinkedBlockingQueue<>();
        this.chunkPackageTasks = new ConcurrentLinkedQueue<>();
        this.deflater = new Deflater( Deflater.DEFAULT_COMPRESSION );
        this.players = HashObjObjMaps.newMutableMap();
    }

    // ==================================== WORLD ADAPTER ==================================== //

    public String getWorldName() {
        return this.worldDir.getName();
    }

    public String getLevelName() {
        return this.levelName;
    }

    public Location getSpawnLocation() {
        return new Location( this, this.spawnX, this.spawnY, this.spawnZ );
    }

    public Block getBlockAt( Vector vector ) {
        return null;
    }

    @SuppressWarnings( "unchecked" )
    public <T> T getGamerule( Gamerule<T> gamerule ) {
        return this.gamerules.containsKey( gamerule ) ? (T) this.gamerules.get( gamerule ) : null;
    }

    @Override
    public void addPlayer( EntityPlayer player ) {
        // Schedule sending spawn region chunks:
        int spawnRadius = this.server.getServerConfig().getAmountOfChunksForSpawnArea() * 16;
        if ( spawnRadius < 64 ) {
            spawnRadius = 64; // We need at least 64 blocks to spawn
        }

        final int minBlockX = this.spawnX - spawnRadius;
        final int minBlockZ = this.spawnZ - spawnRadius;
        final int maxBlockX = this.spawnX + spawnRadius;
        final int maxBlockZ = this.spawnZ + spawnRadius;

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
    public void tick() {
        // ---------------------------------------
        // Tick the chunk cache to get rid of Chunks
        this.chunkCache.tick();

        // ---------------------------------------
        // Chunk packages are done in main thread in order to be able to
        // cache packets without possibly getting into race conditions:
        if ( !this.chunkPackageTasks.isEmpty() ) {
            // One chunk per tick at max:
            AsyncChunkPackageTask task = this.chunkPackageTasks.poll();
            AnvilChunk chunk = (AnvilChunk) task.getChunk();
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
            task.getCallback().invoke( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ), batch );
        }

        // ---------------------------------------
        // Perform regular updates:
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
        final int spawnXChunk = CoordinateUtils.fromBlockToChunk( this.spawnX );
        final int spawnZChunk = CoordinateUtils.fromBlockToChunk( this.spawnZ );

        TwoArgDelegate<Long, Packet> sendDelegate = new TwoArgDelegate<Long, Packet>() {
            @Override
            public void invoke( Long chunkHash, Packet chunkPacket ) {
                player.getConnection().sendWorldChunk( chunkHash, chunkPacket );
            }
        };

        this.getOrLoadChunk( x, z, true, new Delegate<ChunkAdapter>() {
            @Override
            public void invoke( ChunkAdapter chunk ) {
                chunk.packageChunk( sendDelegate );

                if ( x == spawnXChunk && z == spawnZChunk ) {
                    logger.info( "Spawned Player " + player.getId() + " on chunk " + spawnXChunk + "; " + spawnZChunk );
                    players.put( player, chunk );
                    chunk.addPlayer( player );
                }
            }
        } );
    }

    @Override
    public void movePlayerToChunk( int x, int z, EntityPlayer player ) {
        ChunkAdapter oldChunk = this.players.get( player );
        getOrLoadChunk( x, z, true, new Delegate<ChunkAdapter>() {
            @Override
            public void invoke( ChunkAdapter newChunk ) {
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
     * @param chunk    The chunk that was told to package itself
     * @param callback The callback to be invoked once the chunk is packaged
     */
    void notifyPackageChunk( AnvilChunk chunk, TwoArgDelegate<Long, Packet> callback ) {
        AsyncChunkPackageTask task = new AsyncChunkPackageTask( chunk, callback );
        this.chunkPackageTasks.add( task );
    }

    /**
     * Loads all information about the world given inside the level.dat file found
     * in the world's root directory.
     *
     * @throws IOException Thrown in case the level.dat file could not be loaded
     */
    private void loadLevelDat() throws IOException {
        try {
            File levelDat = new File( this.worldDir, "level.dat" );
            if ( !levelDat.exists() || !levelDat.isFile() ) {
                throw new IOException( "Missing level.dat" );
            }

            NBTTagCompound nbt = NBTTagCompound.readFrom( levelDat, true, ByteOrder.BIG_ENDIAN );
            NBTTagCompound data = nbt.getCompound( "Data", false );
            if ( data == null ) {
                throw new IOException( "level.dat is missing 'Data' tag" );
            }

            if ( data.getInteger( "version", 0 ) != 19133 ) {
                throw new IOException( "unsupported world format" );
            }

            this.levelName = data.getString( "LevelName", "" );
            this.randomSeed = data.getLong( "RandomSeed", 0L );
            this.mapFeatures = data.getByte( "MapFeatures", (byte) 0 ) > 0;
            this.lastPlayed = data.getLong( "LastPlayed", 0L );
            this.allowCommands = data.getByte( "allowCommands", (byte) 0 ) > 0;
            this.hardcore = data.getByte( "hardcore", (byte) 0 ) > 0;
            this.difficulty = GameDifficulty.valueOf( data.getByte( "Difficulty", (byte) 0 ) );
            this.difficultyLocked = data.getByte( "DifficultyLocked", (byte) 0 ) > 0;
            this.time = data.getLong( "Time", 0L );
            this.dayTime = data.getLong( "DayTime", 0L );
            this.spawnX = data.getInteger( "SpawnX", 0 );
            this.spawnY = data.getInteger( "SpawnY", 0 );
            this.spawnZ = data.getInteger( "SpawnZ", 0 );
            this.worldBorder = new WorldBorder( this, data );
            this.weather = new Weather( this.random, data );

            // Load gamerules:
            NBTTagCompound gamerules = data.getCompound( "GameRules", false );
            if ( gamerules != null ) {
                for ( Map.Entry<String, Object> entry : gamerules.entrySet() ) {
                    if ( entry.getValue() instanceof String ) {
                        Gamerule gamerule = Gamerule.getByNbtName( entry.getKey() );
                        if ( gamerule != null ) {
                            this.gamerules.put( gamerule, gamerule.createValueFromString( (String) entry.getValue() ) );
                        }
                    }
                }
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

        final int minBlockX = this.spawnX - spawnRadius;
        final int minBlockZ = this.spawnZ - spawnRadius;
        final int maxBlockX = this.spawnX + spawnRadius;
        final int maxBlockZ = this.spawnZ + spawnRadius;

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
     * Loads an anvil world given the path to the world's directory. This operation
     * performs synchronously and will at least load the entire spawn region before
     * completing.
     *
     * @param server The GoMint Server which runs this
     * @param pathToWorld The path to the world's directory
     * @return The anvil world adapter used to access the world
     * @throws IOException Thrown in case the world could not be loaded successfully
     */
    public static AnvilWorldAdapter load( GoMintServer server, File pathToWorld ) throws IOException {
        AnvilWorldAdapter world = new AnvilWorldAdapter( server, pathToWorld );
        world.loadLevelDat();
        world.prepareSpawnRegion();
        world.startAsyncWorker( server.getExecutorService() );
        return world;
    }
}
