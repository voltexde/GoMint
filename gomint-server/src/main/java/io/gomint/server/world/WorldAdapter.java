/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.entity.Player;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.jraknet.PacketReliability;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.GoMintServer;
import io.gomint.server.async.Delegate;
import io.gomint.server.async.Delegate2;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.*;
import io.gomint.server.scheduler.TaskList;
import io.gomint.server.util.ByteUtil;
import io.gomint.server.util.EnumConnector;
import io.gomint.world.Gamerule;
import io.gomint.world.Sound;
import io.gomint.world.World;
import io.gomint.world.block.Block;
import lombok.Getter;
import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.Deflater;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class WorldAdapter implements World {

    // CHECKSTYLE:OFF
    // Static enum converters
    private static final EnumConnector<Sound, SoundMagicNumbers> soundEnumConverter = new EnumConnector<>( Sound.class, SoundMagicNumbers.class );

    // Calculators for light
    @Getter
    private static final BlocklightCalculator blockLightCalculator = new BlocklightCalculator();

    // Shared objects
    @Getter
    protected final GoMintServer server;
    protected final Logger logger;

    // World properties
    protected final File worldDir;
    protected String levelName;
    protected Location spawn;
    protected Map<Gamerule, Object> gamerules = new HashMap<>();
    // Chunk Handling
    protected ChunkCache chunkCache;
    // Entity Handling
    protected EntityManager entityManager;
    // Block ticking
    @Getter
    protected TaskList<Long> tickQueue = new TaskList<>();
    // I/O
    private Deflater deflater;
    private boolean asyncWorkerRunning;
    private BlockingQueue<AsyncChunkTask> asyncChunkTasks;
    private Queue<AsyncChunkPackageTask> chunkPackageTasks;
    // Player handling
    private ObjObjMap<EntityPlayer, ChunkAdapter> players;

    protected WorldAdapter( GoMintServer server, File worldDir ) {
        this.server = server;
        this.logger = LoggerFactory.getLogger( "World-" + worldDir.getName() );
        this.worldDir = worldDir;
        this.entityManager = new EntityManager( this );
        this.players = HashObjObjMaps.newMutableMap();
        this.deflater = new Deflater( Deflater.DEFAULT_COMPRESSION );
        this.asyncChunkTasks = new LinkedBlockingQueue<>();
        this.chunkPackageTasks = new ConcurrentLinkedQueue<>();
        this.startAsyncWorker( server.getExecutorService() );
    }
    // CHECKSTYLE:ON

    // ==================================== GENERAL ACCESSORS ==================================== //

    /**
     * Get the current view of players on this world.
     *
     * @return The Collection View of the Players currently on this world
     */
    public Map<EntityPlayer, ChunkAdapter> getPlayers0() {
        return players;
    }

    public Collection<Player> getPlayers() {
        Collection<Player> playerReturn = new HashSet<>();
        playerReturn.addAll( players.keySet() );
        return playerReturn;
    }

    @Override
    public void playSound( Location location, Sound sound, byte pitch, int extraData ) {
        PacketWorldSoundEvent soundPacket = new PacketWorldSoundEvent();
        soundPacket.setType( soundEnumConverter.convert( sound ) );
        soundPacket.setPitch( pitch );
        soundPacket.setExtraData( extraData );
        soundPacket.setPosition( location );
        broadcast( PacketReliability.RELIABLE, 0, soundPacket );
    }

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
    public <T extends Block> T getBlockAt( Vector vector ) {
        int x = (int) vector.getX();
        int y = (int) vector.getY();
        int z = (int) vector.getZ();

        final ChunkAdapter chunk = this.getChunk( CoordinateUtils.fromBlockToChunk( x ), CoordinateUtils.fromBlockToChunk( z ) );
        if ( chunk == null ) {
            // TODO: Generate world
            return null;
        }

        // Get correct block out of the blocks factory
        return chunk.getBlockAt( x & 0xF, y, z & 0xF );
    }

    public void setBlockLight( Vector vector, byte lightLevel ) {
        int x = (int) vector.getX();
        int y = (int) vector.getY();
        int z = (int) vector.getZ();

        final ChunkAdapter chunk = this.getChunk( CoordinateUtils.fromBlockToChunk( x ), CoordinateUtils.fromBlockToChunk( z ) );
        if ( chunk == null ) {
            // TODO: Generate world
            return;
        }

        chunk.setBlockLight( x & 0xF, y, z & 0xF, lightLevel );
    }

    public void setBlockId( Vector vector, int blockId ) {
        int x = (int) vector.getX();
        int y = (int) vector.getY();
        int z = (int) vector.getZ();

        final ChunkAdapter chunk = this.getChunk( CoordinateUtils.fromBlockToChunk( x ), CoordinateUtils.fromBlockToChunk( z ) );
        if ( chunk == null ) {
            // TODO: Generate world
            return;
        }

        chunk.setBlock( x & 0xF, y, z & 0xF, blockId );
    }

    public void setBlockData( Vector vector, byte data ) {
        int x = (int) vector.getX();
        int y = (int) vector.getY();
        int z = (int) vector.getZ();

        final ChunkAdapter chunk = this.getChunk( CoordinateUtils.fromBlockToChunk( x ), CoordinateUtils.fromBlockToChunk( z ) );
        if ( chunk == null ) {
            // TODO: Generate world
            return;
        }

        chunk.setData( x & 0xF, y, z & 0xF, data );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T getGamerule( Gamerule<T> gamerule ) {
        return (T) this.gamerules.get( gamerule );
    }

    // ==================================== UPDATING ==================================== //

    /**
     * Ticks the world and updates what needs to be updated.
     *
     * @param currentTimeMS The current time in milliseconds. Used to reduce the number of calls to System#currentTimeMillis()
     * @param dT            The delta from the full second which has been calculated in the last tick
     */
    public void update( long currentTimeMS, float dT ) {
        // ---------------------------------------
        // Tick the chunk cache to get rid of Chunks
        this.chunkCache.tick( currentTimeMS );

        // ---------------------------------------
        // Update all blocks
        while ( this.tickQueue.getNextTaskTime() < currentTimeMS ) {
            Long blockToUpdate = this.tickQueue.getNextElement();
            if ( blockToUpdate == null ) {
                break;
            }

            // Get the block
            Block block = getBlockAt( CoordinateUtils.fromLong( blockToUpdate ) );
            if ( block != null ) {
                try {
                    io.gomint.server.world.block.Block block1 = (io.gomint.server.world.block.Block) block;
                    long next = block1.update( currentTimeMS, dT );

                    // Check for abort value ( -1 )
                    if ( next == -1 ) {
                        continue;
                    }

                    // Reschedule if needed
                    if ( next > currentTimeMS ) {
                        this.tickQueue.add( next, blockToUpdate );
                    }
                } catch ( Exception e ) {
                    logger.error( "Error whilst ticking block @ " + blockToUpdate, e );
                }
            }
        }

        // ---------------------------------------
        // Update all entities
        this.entityManager.update( currentTimeMS, dT );

        // ---------------------------------------
        // Chunk packages are done in main thread in order to be able to
        // cache packets without possibly getting into race conditions:
        if ( !this.chunkPackageTasks.isEmpty() ) {
            // One chunk per tick at max:
            AsyncChunkPackageTask task = this.chunkPackageTasks.poll();
            ChunkAdapter chunk = this.getChunk( task.getX(), task.getZ() );
            if ( chunk == null ) {
                final Object lock = new Object();

                this.getOrLoadChunk( task.getX(), task.getZ(), false, new Delegate<ChunkAdapter>() {
                    @Override
                    public void invoke( ChunkAdapter arg ) {
                        synchronized ( lock ) {
                            packageChunk( arg, task.getCallback() );
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

    // ==================================== ENTITY MANAGEMENT ==================================== //

    /**
     * Adds a new player to this world and schedules all world chunk packets required for spawning
     * the player for send.
     *
     * @param player The player entity to add to the world
     */
    public void addPlayer( EntityPlayer player ) {
        // Schedule sending spawn region chunks:
        final int minBlockX = (int) ( this.spawn.getX() - 64 );
        final int minBlockZ = (int) ( this.spawn.getZ() - 64 );
        final int maxBlockX = (int) ( this.spawn.getX() + 64 );
        final int maxBlockZ = (int) ( this.spawn.getZ() + 64 );

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

    /**
     * Removes a player from this world and cleans up its references
     *
     * @param player The player entity which should be removed from the world
     */
    public void removePlayer( EntityPlayer player ) {
        ChunkAdapter chunkAdapter = this.players.remove( player );
        if ( chunkAdapter != null ) {
            chunkAdapter.removePlayer( player );
        }
    }

    /**
     * Gets an entity given its unique ID.
     *
     * @param entityId The entity's unique ID
     * @return The entity if found or null otherwise
     */
    public Entity findEntity( long entityId ) {
        return this.entityManager.findEntity( entityId );
    }

    /**
     * Spawns the given entity at the specified position.
     *
     * @param entity    The entity to spawn
     * @param positionX The x coordinate to spawn the entity at
     * @param positionY The y coordinate to spawn the entity at
     * @param positionZ The z coordinate to spawn the entity at
     */
    public void spawnEntityAt( Entity entity, float positionX, float positionY, float positionZ ) {
        this.entityManager.spawnEntityAt( entity, positionX, positionY, positionZ );
    }

    /**
     * Spawns the given entity at the specified position with the specified rotation.
     *
     * @param entity    The entity to spawn
     * @param positionX The x coordinate to spawn the entity at
     * @param positionY The y coordinate to spawn the entity at
     * @param positionZ The z coordinate to spawn the entity at
     * @param yaw       The yaw value of the entity ; will be applied to both the entity's body and head
     * @param pitch     The pitch value of the entity
     */
    public void spawnEntityAt( Entity entity, float positionX, float positionY, float positionZ, float yaw, float pitch ) {
        this.entityManager.spawnEntityAt( entity, positionX, positionY, positionZ, yaw, pitch );
    }

    /**
     * Despawns an entity given its unique ID.
     *
     * @param entityId The unique ID of the entity
     */
    public void despawnEntity( long entityId ) {
        this.entityManager.despawnEntity( entityId );
    }

    // ==================================== CHUNK MANAGEMENT ==================================== //

    /**
     * Gets the chunk at the specified coordinates. If the chunk is currently not available
     * it will be loaded or generated.
     *
     * @param x The x-coordinate of the chunk
     * @param z The z-coordinate of the chunk
     * @return The chunk if available or null otherwise
     */
    public ChunkAdapter getChunk( int x, int z ) {
        return this.chunkCache.getChunk( x, z );
    }

    /**
     * Gets a chunk asynchronously. This allows to load or generate the chunk if it is not yet available
     * and then return it once it gets available. The callback is guaranteed to be invoked: if the chunk
     * could not be loaded nor be generated it will be passed null as its argument.
     *
     * @param x        The x-coordinate of the chunk
     * @param z        The z-coordinate of the chunk
     * @param generate Whether or not to generate teh chunk if it does not yet exist
     * @param callback The callback to be invoked once the chunk is available
     */
    public void getOrLoadChunk( int x, int z, boolean generate, Delegate<ChunkAdapter> callback ) {
        // Early out:
        ChunkAdapter chunk = this.chunkCache.getChunk( x, z );
        if ( chunk != null ) {
            callback.invoke( chunk );
            return;
        }

        // Schedule this chunk for asynchronous loading:
        AsyncChunkLoadTask task = new AsyncChunkLoadTask( x, z, generate, callback );
        this.asyncChunkTasks.offer( task );
    }

    /**
     * Send a chunk of this world to the client
     *
     * @param x      The x-coordinate of the chunk
     * @param z      The z-coordinate of the chunk
     * @param player The player we want to send the chunk to
     */
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

    /**
     * Move a player to a new chunk. This is done so we know which player is in which chunk so we can unload unneeded
     * Chunks better and faster.
     *
     * @param x      The x-coordinate of the chunk
     * @param z      The z-coordinate of the chunk
     * @param player The player which should be set into the chunk
     */
    public void movePlayerToChunk( int x, int z, EntityPlayer player ) {
        ChunkAdapter oldChunk = this.players.get( player );
        getOrLoadChunk( x, z, true, new Delegate<ChunkAdapter>() {
            @Override
            public void invoke( ChunkAdapter newChunk ) {
                if ( oldChunk == null ) {
                    newChunk.addPlayer( player );
                    WorldAdapter.this.players.put( player, newChunk );
                }

                if ( oldChunk != null && !oldChunk.equals( newChunk ) ) {
                    oldChunk.removePlayer( player );
                    newChunk.addPlayer( player );
                    WorldAdapter.this.players.put( player, newChunk );
                }
            }
        } );
    }

    /**
     * Prepares the region surrounding the world's spawn point.
     *
     * @throws IOException Throws in case the spawn region could not be loaded nor generated
     */
    protected void prepareSpawnRegion() throws IOException {
        final int spawnRadius = this.server.getServerConfig().getAmountOfChunksForSpawnArea() * 16;
        if ( spawnRadius == 0 ) {
            return;
        }

        final int minBlockX = (int) ( this.spawn.getX() - spawnRadius );
        final int minBlockZ = (int) ( this.spawn.getZ() - spawnRadius );
        final int maxBlockX = (int) ( this.spawn.getX() + spawnRadius );
        final int maxBlockZ = (int) ( this.spawn.getZ() + spawnRadius );

        final int minChunkX = CoordinateUtils.fromBlockToChunk( minBlockX );
        final int minChunkZ = CoordinateUtils.fromBlockToChunk( minBlockZ );
        final int maxChunkX = CoordinateUtils.fromBlockToChunk( maxBlockX );
        final int maxChunkZ = CoordinateUtils.fromBlockToChunk( maxBlockZ );

        for ( int i = minChunkZ; i <= maxChunkZ; ++i ) {
            for ( int j = minChunkX; j <= maxChunkX; ++j ) {
                ChunkAdapter chunkAdapter = this.loadChunk( i, j, true );
                if ( chunkAdapter == null ) {
                    throw new IOException( "Could not load spawn chunk @ " + i + " " + j );
                }
            }
        }
    }

    /**
     * Load a Chunk from the underlying implementation
     *
     * @param x        The x coordinate of the chunk we want to load
     * @param z        The x coordinate of the chunk we want to load
     * @param generate A boolean which decides whether or not the chunk should be generated when not found
     * @return The loaded or generated Chunk
     */
    protected abstract ChunkAdapter loadChunk( int x, int z, boolean generate );

    /**
     * Saves the given chunk to its respective region file. The respective region file
     * is created automatically if it does not yet exist.
     *
     * @param chunk The chunk to be saved
     */
    protected abstract void saveChunk( ChunkAdapter chunk );

    /**
     * Saves the given chunk to its region file asynchronously.
     *
     * @param chunk The chunk to save
     */
    public void saveChunkAsynchronously( ChunkAdapter chunk ) {
        AsyncChunkSaveTask task = new AsyncChunkSaveTask( chunk );
        this.asyncChunkTasks.add( task );
    }

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
     * Package a Chunk into a ChunkData Packet for Raknet. This is done to enable caching of those packets.
     *
     * @param chunk    The chunk which should be packed
     * @param callback The callback which should be invoked when the packing has been done
     */
    void packageChunk( ChunkAdapter chunk, Delegate2<Long, Packet> callback ) {
        PacketWorldChunk packet = chunk.createPackagedData();

        PacketBuffer buffer = new PacketBuffer( packet.estimateLength() + 1 );
        buffer.writeByte( packet.getId() );
        packet.serialize( buffer );

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        try {
            ByteUtil.writeVarInt( buffer.getPosition(), dout );
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

        chunk.setCachedPacket( batch );
        callback.invoke( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ), batch );
    }

    // ==================================== NETWORKING HELPERS ==================================== //

    /**
     * Broadcasts the given packet to all players in this world.
     *
     * @param reliability     The reliability to send the packet with
     * @param orderingChannel The ordering channel to send the packet on
     * @param packet          The packet to send
     */
    public void broadcast( PacketReliability reliability, int orderingChannel, Packet packet ) {
        // Avoid duplicate arrays containing the very same data:
        PacketBuffer buffer = new PacketBuffer( packet.estimateLength() == -1 ? 64 : packet.estimateLength() + 2 );
        buffer.writeByte( (byte) 0xFE );
        buffer.writeByte( packet.getId() );
        packet.serialize( buffer );

        // Avoid duplicate array copies:
        byte[] payload;

        if ( buffer.getRemaining() == 0 ) {
            payload = buffer.getBuffer();
        } else {
            payload = new byte[buffer.getPosition() - buffer.getBufferOffset()];
            System.arraycopy( buffer.getBuffer(), buffer.getBufferOffset(), payload, 0, buffer.getPosition() - buffer.getBufferOffset() );
        }

        // Send directly:
        for ( EntityPlayer player : this.players.keySet() ) {
            player.getConnection().getConnection().send( reliability, orderingChannel, payload );
        }
    }

    public void broadCastToChunk( int chunkX, int chunkZ, PacketReliability reliability, int orderingChannel, Packet packet ) {
        // Avoid duplicate arrays containing the very same data:
        PacketBuffer buffer = new PacketBuffer( packet.estimateLength() == -1 ? 64 : packet.estimateLength() + 2 );
        buffer.writeByte( (byte) 0xFE );
        buffer.writeByte( packet.getId() );
        packet.serialize( buffer );

        // Avoid duplicate array copies:
        byte[] payload;

        if ( buffer.getRemaining() == 0 ) {
            payload = buffer.getBuffer();
        } else {
            payload = new byte[buffer.getPosition() - buffer.getBufferOffset()];
            System.arraycopy( buffer.getBuffer(), buffer.getBufferOffset(), payload, 0, buffer.getPosition() - buffer.getBufferOffset() );
        }

        // Send directly:
        ChunkAdapter adapter = getChunk( chunkX, chunkZ );
        for ( EntityPlayer player : adapter.getPlayers() ) {
            player.getConnection().getConnection().send( reliability, orderingChannel, payload );
        }
    }

    // ==================================== ASYNCHRONOUS WORKER ==================================== //

    /**
     * Starts the asynchronous worker thread used by the world to perform I/O operations for chunks.
     */
    private void startAsyncWorker( ExecutorService executorService ) {
        executorService.execute( new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName( Thread.currentThread().getName() + " [Async World I/O: " + WorldAdapter.this.getWorldName() + "]" );
                WorldAdapter.this.asyncWorkerLoop();
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
                if ( task == null ) {
                    continue;
                }

                long start = System.currentTimeMillis();

                ChunkAdapter chunk = null;
                switch ( task.getType() ) {
                    case LOAD:
                        AsyncChunkLoadTask load = (AsyncChunkLoadTask) task;
                        chunk = this.loadChunk( load.getX(), load.getZ(), load.isGenerate() );
                        load.getCallback().invoke( chunk );
                        break;

                    case SAVE:
                        AsyncChunkSaveTask save = (AsyncChunkSaveTask) task;
                        chunk = save.getChunk();
                        this.saveChunk( chunk );
                        break;

                    default:
                        // Log some error when this happens

                        break;
                }

                if ( chunk != null ) {
                    logger.debug( "Done chunk work " + task.getType() + " for " + chunk.getX() + " " + chunk.getZ() + " in " + ( System.currentTimeMillis() - start ) + "ms" );
                }
            } catch ( Throwable cause ) {
                // Catching throwable in order to make sure no uncaught exceptions puts
                // the asynchronous worker into nirvana:

            }
        }
    }

    /**
     * Send the block given under the position to all players in the chunk of the block
     *
     * @param pos The position of the block to update
     */
    public void updateBlock( Vector pos ) {
        io.gomint.server.world.block.Block block = getBlockAt( pos );

        PacketUpdateBlock updateBlock = new PacketUpdateBlock();
        updateBlock.setPosition( pos );
        updateBlock.setBlockId( block.getBlockId() );
        updateBlock.setPrioAndMetadata( (byte) ( 0xb << 4 | ( block.getBlockData() & 0xf ) ) );

        broadCastToChunk( CoordinateUtils.fromBlockToChunk( (int) pos.getX() ), CoordinateUtils.fromBlockToChunk( (int) pos.getZ() ), PacketReliability.RELIABLE, 0, updateBlock );
    }

}
