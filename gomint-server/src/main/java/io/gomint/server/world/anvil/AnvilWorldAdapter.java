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
import io.gomint.server.async.Delegate;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.Protocol;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketBA;
import io.gomint.server.network.packet.PacketBatch;
import io.gomint.server.network.packet.PacketMovePlayer;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.zip.Deflater;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class AnvilWorldAdapter extends WorldAdapter {

	/**
	 * Loads an anvil world given the path to the world's directory. This operation
	 * performs synchronously and will at least load the entire spawn region before
	 * completing.
	 *
	 * @param pathToWorld The path to the world's directory
	 *
	 * @return The anvil world adapter used to access the world
	 * @throws IOException Thrown in case the world could not be loaded successfully
	 */
	public static AnvilWorldAdapter load( File pathToWorld ) throws IOException {
		AnvilWorldAdapter world = new AnvilWorldAdapter( pathToWorld );
		world.loadLevelDat();
		world.prepareSpawnRegion();
		world.startAsyncWorker();
		return world;
	}

	// ==================================== FIELDS ==================================== //

	// Shared objects
	private final Random random = new Random();

	// World properties
	private final File           worldDir;
	private       String         levelName;
	private       long           randomSeed;
	private       boolean        mapFeatures;
	private       long           lastPlayed;
	private       boolean        allowCommands;
	private       boolean        hardcore;
	private       GameDifficulty difficulty;
	private       boolean        difficultyLocked;
	private       long           time;
	private       long           dayTime;
	private       int            spawnX;
	private       int            spawnY;
	private       int            spawnZ;
	private       WorldBorder    worldBorder;
	private       Weather        weather;
	private Map<Gamerule, Object> gamerules = new HashMap<>();

	// Chunk Handling
	private AnvilChunkCache chunkCache;

	// I/O
	private int                          regionXRead;
	private int                          regionZRead;
	private RegionFile                   regionFileRead;
	private boolean                      asyncWorkerRunning;
	private Queue<AsyncChunkTask>        asyncChunkTasks;
	private Queue<AsyncChunkPackageTask> chunkPackageTasks;
	private Deflater                     deflater;

	AnvilWorldAdapter( final File worldDir ) {
		this.worldDir = worldDir;
		this.chunkCache = new AnvilChunkCache( this );
		this.asyncChunkTasks = new LinkedList<>();
		this.chunkPackageTasks = new LinkedList<>();
		this.deflater = new Deflater( Deflater.DEFAULT_COMPRESSION );
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
		final int spawnRadius = 64; // TODO: Make configurable

		final int minBlockX = this.spawnX - spawnRadius;
		final int minBlockZ = this.spawnZ - spawnRadius;
		final int maxBlockX = this.spawnX + spawnRadius;
		final int maxBlockZ = this.spawnZ + spawnRadius;

		final int minChunkX = CoordinateUtils.fromBlockToChunk( minBlockX );
		final int minChunkZ = CoordinateUtils.fromBlockToChunk( minBlockZ );
		final int maxChunkX = CoordinateUtils.fromBlockToChunk( maxBlockX );
		final int maxChunkZ = CoordinateUtils.fromBlockToChunk( maxBlockZ );

		Delegate<Packet> sendDelegate = new Delegate<Packet>() {
			@Override
			public void invoke( Packet arg ) {
				player.getConnection().sendWorldChunk( arg );
			}
		};

		for ( int i = minChunkZ; i <= maxChunkZ; ++i ) {
			for ( int j = minChunkX; j <= maxChunkX; ++j ) {
				this.getChunk( j, i ).packageChunk( sendDelegate );
			}
		}
	}

	@Override
	public void tick() {
		// ---------------------------------------
		// Chunk packages are done in main thread in order to be able to
		// cache packets without possibly getting into race conditions:
		synchronized ( this.chunkPackageTasks ) {
			if ( !this.chunkPackageTasks.isEmpty() ) {
				// One chunk per tick at max:
				AsyncChunkPackageTask task   = this.chunkPackageTasks.poll();
				AnvilChunk            chunk  = (AnvilChunk) task.getChunk();
				PacketWorldChunk      packet = chunk.createPackagedData();

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

				chunk.dirty = false;
				chunk.cachedPacket = batch;
				task.getCallback().invoke( batch );
			}
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
		synchronized ( this.asyncChunkTasks ) {
			this.asyncChunkTasks.add( task );
			this.asyncChunkTasks.notify();
		}
	}

	// ==================================== INTERNALS ==================================== //

	/**
	 * Notifies the world that the given chunk was told to package itself. This will effectively
	 * produce an asynchronous chunk task which will be completed by the asynchronous worker thread.
	 *
	 * @param chunk    The chunk that was told to package itself
	 * @param callback The callback to be invoked once the chunk is packaged
	 */
	void notifyPackageChunk( AnvilChunk chunk, Delegate<Packet> callback ) {
		AsyncChunkPackageTask task = new AsyncChunkPackageTask( chunk, callback );
		synchronized ( this.chunkPackageTasks ) {
			this.chunkPackageTasks.add( task );
		}
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

			NBTTagCompound nbt  = NBTTagCompound.readFrom( levelDat, true, ByteOrder.BIG_ENDIAN );
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
		final int spawnRadius = 64; // TODO: Make configurable

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
	private void startAsyncWorker() {
		Thread worker = new Thread( new Runnable() {
			@Override
			public void run() {
				AnvilWorldAdapter.this.asyncWorkerLoop();
			}
		} );

		this.asyncWorkerRunning = true;

		worker.setDaemon( true );
		worker.setName( this.getWorldName() + "-Worker" );
		worker.start();
	}

	/**
	 * Main loop of the world's asynchronous worker thread.
	 */
	private void asyncWorkerLoop() {
		while ( this.asyncWorkerRunning ) {
			synchronized ( this.asyncChunkTasks ) {
				while ( !this.asyncChunkTasks.isEmpty() ) {
					try {
						AsyncChunkTask task = this.asyncChunkTasks.poll();
						AnvilChunk     chunk;
						switch ( task.getType() ) {
							case LOAD:
								AsyncChunkLoadTask load = (AsyncChunkLoadTask) task;
								chunk = this.loadChunk( load.getX(), load.getZ(), load.isGenerate() );
								load.getCallback().invoke( chunk );
								break;

							case SAVE:
								// TODO: Implement saving chunks
								break;
						}
					} catch ( Throwable cause ) {
						// Catching throwable in order to make sure no uncaught exceptions puts
						// the asynchronous worker into nirvana:

					}
				}

				// Wait for new chunk tasks:
				try {
					this.asyncChunkTasks.wait();
				} catch ( InterruptedException ignored ) {
					// No need to care about spurious wakeups as they would end up back here again:
					// ._.
				}
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
	 *
	 * @return The chunk that was just loaded or null if it could not be loaded
	 */
	private AnvilChunk loadChunk( int x, int z, boolean generate ) {
		AnvilChunk chunk = this.chunkCache.getChunk( x, z );
		if ( chunk == null ) {
			try {
				int        regionX    = CoordinateUtils.fromChunkToRegion( x );
				int        regionZ    = CoordinateUtils.fromChunkToRegion( z );
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

}
