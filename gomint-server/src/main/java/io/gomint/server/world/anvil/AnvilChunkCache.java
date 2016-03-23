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

/**
 * @author BlackyPaw
 * @version 1.0
 */
class AnvilChunkCache extends ChunkCacheAdapter {

    private static final Logger logger = LoggerFactory.getLogger( AnvilChunkCache.class );

	// ==================================== FIELDS ==================================== //
	private final AnvilWorldAdapter                         world;
	private final LongObjMap<SoftReference<AnvilChunk>>     cachedChunks;

    /**
     * New cache for Anvil format Chunks
     * @param world for which we cache chunks
     */
	public AnvilChunkCache( final AnvilWorldAdapter world ) {
		this.world = world;
		this.cachedChunks = HashLongObjMaps.newMutableMap();
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

            SoftReference<AnvilChunk> reference = this.cachedChunks.get( chunkHash );
            if ( reference == null ) return null;

            AnvilChunk chunk = reference.get();
            if ( chunk == null ) {
                this.cachedChunks.remove( chunkHash );
            }

            return chunk;
        }
	}

    @Override
    public void tick() {
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

                // Fast skip if chunk is whitelisted cause of view distance
                if ( viewDistanceSet.contains( chunkHash ) ) {
                    continue;
                }

                IntPair intPair = CoordinateUtils.toIntPair( chunkHash );

                // Skip if this chunk is a spawn chunk
                if ( this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea() > 0 ) {
                    if ( intPair.getX() <= spawnXChunk + spawnAreaSize && intPair.getZ() <= spawnZChunk + spawnAreaSize ) {
                        continue;
                    }
                }

                // Check if there is any chunk specific reasons against a GC
                SoftReference<AnvilChunk> chunkSoftReference = this.cachedChunks.get( chunkHash );
                if ( chunkSoftReference == null ) continue; // Chunk already has been gced? o.O

                AnvilChunk chunk = chunkSoftReference.get();

                // If the JVM decided it needed the space we can remove the reference to the chunk completely
                if ( chunk == null ) {
                    logger.debug( "Removed chunk " + intPair.getX() + "; " + intPair.getZ() + ": The JVM cleared this chunk" );
                    this.cachedChunks.remove( chunkHash );
                    continue;
                }

                // Ask this chunk if he wants to be gced
                if ( chunk.canBeGCed() ) {
                    logger.debug( "Removed chunk " + intPair.getX() + "; " + intPair.getZ() + ": Chunk can be gced" );
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
            this.cachedChunks.put( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ), new SoftReference<>( chunk ) );
        }
	}
}
