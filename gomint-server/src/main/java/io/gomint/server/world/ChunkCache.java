/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.entity.EntityPlayer;
import net.openhft.koloboke.collect.LongCursor;
import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class ChunkCache {

    private static final Logger LOGGER = LoggerFactory.getLogger( ChunkCache.class );

    // CHECKSTYLE:OFF
    // ==================================== FIELDS ==================================== //
    private final WorldAdapter world;
    private final LongObjMap<ChunkAdapter> cachedChunks;
    private boolean enableAutoSave;
    private long autoSaveInterval;

    public ChunkCache( WorldAdapter world ) {
        this.world = world;
        this.cachedChunks = HashLongObjMaps.newMutableMap();
        this.enableAutoSave = world.getServer().getServerConfig().isAutoSave();
        this.autoSaveInterval = world.getServer().getServerConfig().getAutoSaveInterval();
    }
    // CHECKSTYLE:ON

    /**
     * Ticking for Chunk GC
     *
     * @param currentTimeMS The current time in milliseconds. Used to reduce the number of calls to System#currentTimeMillis()
     */
    public void tick( long currentTimeMS ) {
        int spawnXChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getX() );
        int spawnZChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getZ() );

        int spawnAreaSize = this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea();

        // Check for gc
        synchronized ( this.cachedChunks ) {
            // Calculate the hashes which are used by players viewdistances
            LongSet viewDistanceSet = HashLongSets.newMutableSet();
            for ( Map.Entry<EntityPlayer, ChunkAdapter> playerChunkAdapterEntry : this.world.getPlayers0().entrySet() ) {
                int viewDistance = playerChunkAdapterEntry.getKey().getViewDistance();
                ChunkAdapter chunk = playerChunkAdapterEntry.getValue();

                for ( int x = chunk.getX() - viewDistance; x < chunk.getX() + viewDistance; x++ ) {
                    for ( int z = chunk.getZ() - viewDistance; z < chunk.getZ() + viewDistance; z++ ) {
                        viewDistanceSet.add( CoordinateUtils.toLong( x, z ) );
                    }
                }
            }

            // Copy over the current loaded chunk hashes
            LongSet toRemoveHashes = null;
            LongCursor loadedHashes = this.cachedChunks.keySet().cursor();

            while ( loadedHashes.moveNext() ) {
                long chunkHash = loadedHashes.elem();

                ChunkAdapter chunk = this.cachedChunks.get( chunkHash );
                if ( chunk == null ) {
                    continue;
                }

                boolean readyForGC = false;
                if ( !viewDistanceSet.contains( chunkHash ) ) {
                    // Skip if this chunk is a spawn chunk
                    boolean isSpawnChunk = false;

                    int x = (int) ( chunkHash >> 32 );
                    int z = (int) ( chunkHash & 0xFFFFFFFF ) + Integer.MIN_VALUE;

                    if ( this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea() > 0 ) {
                        if ( x <= spawnXChunk + spawnAreaSize && z <= spawnZChunk + spawnAreaSize ) {
                            isSpawnChunk = true;
                        }
                    }

                    // Ask this chunk if he wants to be gced
                    if ( !isSpawnChunk && chunk.canBeGCed() ) {
                        LOGGER.debug( "Cleaning up chunk @ " + x + " " + z );
                        readyForGC = true;
                    }
                }

                if ( readyForGC || chunk.getLastSavedTimestamp() + this.autoSaveInterval < currentTimeMS ) {
                    this.world.saveChunkAsynchronously( chunk );
                    chunk.setLastSavedTimestamp( currentTimeMS );
                }

                // Ask this chunk if he wants to be gced
                if ( readyForGC ) {
                    if ( toRemoveHashes == null ) {
                        toRemoveHashes = HashLongSets.newMutableSet();
                    }

                    toRemoveHashes.add( chunkHash );
                }
            }

            if ( toRemoveHashes != null ) {
                LongCursor toRemoveCursor = toRemoveHashes.cursor();
                while ( toRemoveCursor.moveNext() ) {
                    this.cachedChunks.remove( toRemoveCursor.elem() );
                }
            }
        }
    }

    // ==================================== CHUNK CACHE ==================================== //

    /**
     * Checks whether or not the specified chunk is currently in cache.
     *
     * @param x The x-coordinate of the chunk
     * @param z The z-coordinate of the chunk
     * @return Whether or not the specified chunk is currently in cache
     */
    public boolean hasChunk( int x, int z ) {
        synchronized ( this.cachedChunks ) {
            return this.cachedChunks.containsKey( CoordinateUtils.toLong( x, z ) );
        }
    }

    /**
     * Attempts to get the specified chunk.
     *
     * @param x The x-coordinate of the chunk
     * @param z The z-coordinate of the chunk
     * @return The chunk if it is loaded or null otherwise
     */
    public ChunkAdapter getChunk( int x, int z ) {
        synchronized ( this.cachedChunks ) {
            long chunkHash = CoordinateUtils.toLong( x, z );
            return this.cachedChunks.get( chunkHash );
        }
    }

    /**
     * Puts the specified chunk into the cache thus making it available to the outside
     *
     * @param chunk The chunk to put into the cache
     */
    public void putChunk( ChunkAdapter chunk ) {
        synchronized ( this.cachedChunks ) {
            this.cachedChunks.put( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ), chunk );
        }
    }

    // ==================================== AUTOSAVE ==================================== //

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
     * @return The auto-save interval
     */
    public long getAutoSaveInterval() {
        return this.autoSaveInterval;
    }

}
