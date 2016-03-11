/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.Weather;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.WorldBorder;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.Block;
import io.gomint.world.GameDifficulty;
import io.gomint.world.Gamerule;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
		return world;
	}

	// ==================================== FIELDS ==================================== //

	// Shared objects
	private final Random random = new Random();

	// World properties
	private final File                  worldDir;
	private       String                levelName;
	private       long                  randomSeed;
	private       boolean               mapFeatures;
	private       long                  lastPlayed;
	private       boolean               allowCommands;
	private       boolean               hardcore;
	private       GameDifficulty        difficulty;
	private       boolean               difficultyLocked;
	private       long                  time;
	private       long                  dayTime;
	private       int                   spawnX;
	private       int                   spawnY;
	private       int                   spawnZ;
	private       WorldBorder           worldBorder;
	private       Weather               weather;
	private       Map<Gamerule, Object> gamerules = new HashMap<>();

	// Chunk Handling
	private AnvilChunkCache chunkCache;

	AnvilWorldAdapter( final File worldDir ) {
		this.worldDir = worldDir;
		this.chunkCache = new AnvilChunkCache();
	}

	// ==================================== ACCESSORS ==================================== //

	/**
	 * Attempts to get the chunk at the specified coordinates. If the chunk is currently not loaded
	 * it will be loaded asynchronously and its yet hollow instance will be returned.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 * @return The chunk if found or null otherwise
	 */
	public ChunkAdapter getChunk( int x, int z ) {
		if ( this.chunkCache.hasChunk( x, z ) ) {
			return this.chunkCache.getChunk( x, z );
		} else {
			// TODO: Implement dynamic loading here
			return new AnvilChunk();
		}
	}

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

	// ==================================== I/O ==================================== //

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
				AnvilChunk chunk = this.loadChunkOrGenerate( j, i );
				if ( chunk != null ) {
					// Just for testing:
					// chunk.getBatchPacket();
					// TODO: Handle fully valid chunk here
					this.chunkCache.putChunk( chunk );
				} else {
					throw new IOException( "Failed to load / generate chunk surrounding spawn region" );
				}
			}
		}
	}

	/**
	 * Attempts to load a chunk first and generates a new one if the chunk was not found.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 *
	 * @return The chunk that was loaded or generated
	 */
	private AnvilChunk loadChunkOrGenerate( int x, int z ) {
		return this.attemptLoadChunk( x, z );
	}

	/**
	 * Attempts to load a chunk from its region file.
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 *
	 * @return The chunk if it could be loaded or null otherwise
	 */
	private AnvilChunk attemptLoadChunk( int x, int z ) {
		try {
			int        regionX    = CoordinateUtils.fromChunkToRegion( x );
			int        regionZ    = CoordinateUtils.fromChunkToRegion( z );
			RegionFile regionFile = new RegionFile( new File( this.worldDir, String.format( "region%sr.%d.%d.mca", File.separator, regionX, regionZ ) ) );
			return regionFile.loadChunk( x, z );
		} catch ( IOException e ) {
			e.printStackTrace();
			return null;
		}
	}

}
