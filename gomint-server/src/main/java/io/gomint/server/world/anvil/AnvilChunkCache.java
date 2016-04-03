/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.util.IntPair;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.ChunkCacheAdapter;
import io.gomint.server.world.CoordinateUtils;

import net.openhft.koloboke.collect.LongCursor;
import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class AnvilChunkCache extends ChunkCacheAdapter {

	// ==================================== FIELDS ==================================== //
	private final AnvilWorldAdapter                         world;
	private final LongObjMap<AnvilChunk>                    cachedChunks;
	private boolean enableAutoSave;
	private long autoSaveInterval;

    /**
     * New cache for Anvil format Chunks
     * @param world for which we cache chunks
     */
	public AnvilChunkCache( final AnvilWorldAdapter world ) {
		super( world );
		this.enableAutoSave = true;
		this.autoSaveInterval = TimeUnit.MINUTES.toMillis( 5L );
	}

	/**
	 * Sets whether or not the auto-save feature should be enabled, i.e. whether or not
	 * chunks should be saved automatically in regular intervals.
	 *
	 * @param autoSave Whether or not to enable the auto-save feature
	 */
	public void setEnableAutosave( boolean autoSave ) {
		this.enableAutoSave = autoSave;
	}

	/**
	 * Checks whether or not the auto-save feature is enabled.
	 *
	 * @return Whether or not the auto-save feature is enabled
	 */
	public boolean isAutosaveEnabled() {
		return this.enableAutoSave;
	}

	/**
	 * Gets the interval in milliseconds in which chunks get saved automatically.
	 *
	 * @param interval The interval in which to save chunks
	 */
	public void setAutoSaveInterval( long interval ) {
		this.autoSaveInterval = interval;
	}

	/**
	 * Gets the interval in milliseconds in which chunks get saved automatically.
	 *
	 * @return The auto-save interval
	 */
	public long getAutoSaveInterval() {
		return this.autoSaveInterval;
	}

	// ==================================== CHUNK CACHE ==================================== //

	@Override
	public boolean hasChunk( int x, int z ) {
        synchronized ( this.cachedChunks ) {
            return this.cachedChunks.containsKey( CoordinateUtils.toLong( x, z ) );
        }
	}

	@Override
	public AnvilChunk getChunk( int x, int z ) {
        synchronized ( this.cachedChunks ) {
            long chunkHash = CoordinateUtils.toLong( x, z );
            return this.cachedChunks.get( chunkHash );
        }
	}

    @Override
    public void tick( long currentTimeMS ) {
        int spawnXChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getX() );
        int spawnZChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getZ() );

        int spawnAreaSize = this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea();

        // Check for gc
        synchronized ( this.cachedChunks ) {
            // Calculate the hashes which are used by players viewdistances
            LongSet viewDistanceSet = HashLongSets.newMutableSet();
            for ( Map.Entry<EntityPlayer, ChunkAdapter> playerChunkAdapterEntry : this.world.getPlayers().entrySet() ) {
                int viewDistance = playerChunkAdapterEntry.getKey().getViewDistance();
                AnvilChunk chunk = (AnvilChunk) playerChunkAdapterEntry.getValue();

                for ( int x = chunk.getX() - viewDistance; x < chunk.getX() + viewDistance; x++ ) {
                    for ( int z = chunk.getZ() - viewDistance; z < chunk.getZ() + viewDistance; z++ ) {
                        viewDistanceSet.add( CoordinateUtils.toLong( x, z ) );
                    }
                }
            }

            // Copy over the current loaded chunk hashes
            LongCursor longs = HashLongSets.newMutableSet( this.cachedChunks.keySet() ).cursor();
            while ( longs.moveNext() ) {
                long chunkHash = longs.elem();

	            AnvilChunk chunk = this.cachedChunks.get( chunkHash );
	            if ( chunk == null ) {
		            continue;
	            }

	            boolean readyForGC = false;
	            if ( !viewDistanceSet.contains( chunkHash ) ) {
		            // Skip if this chunk is a spawn chunk
		            boolean isSpawnChunk = false;
		            IntPair intPair = CoordinateUtils.toIntPair( chunkHash );
		            if ( this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea() > 0 ) {
			            if ( intPair.getX() <= spawnXChunk + spawnAreaSize && intPair.getZ() <= spawnZChunk + spawnAreaSize ) {
				            isSpawnChunk = true;
			            }
		            }

		            // Ask this chunk if he wants to be gced
		            if ( !isSpawnChunk && chunk.canBeGCed() ) {
			            readyForGC = true;
		            }
	            }

	            if ( readyForGC || chunk.getLastSavedTimestamp() + this.autoSaveInterval < currentTimeMS ) {
		            this.world.saveChunkAsynchronously( chunk );
		            chunk.setLastSavedTimestamp( currentTimeMS );
	            }

                // Ask this chunk if he wants to be gced
                if ( readyForGC ) {
                    this.cachedChunks.remove( chunkHash );
                }
            }
        }
    }

    /**
	 * Puts the specified chunk into the cache thus making it available to the outside
	 *
	 * @param chunk The chunk to put into the cache
	 */
	void putChunk( AnvilChunk chunk ) {
        synchronized ( this.cachedChunks ) {
            this.cachedChunks.put( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ), chunk );
        }
	}
}
