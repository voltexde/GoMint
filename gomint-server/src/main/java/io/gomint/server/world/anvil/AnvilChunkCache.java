/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.ChunkCacheAdapter;
import io.gomint.server.world.CoordinateUtils;
import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import net.openhft.koloboke.function.LongObjPredicate;

import java.lang.ref.SoftReference;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class AnvilChunkCache extends ChunkCacheAdapter {

	// ==================================== FIELDS ==================================== //
	private final AnvilWorldAdapter                         world;
	private final LongObjMap<SoftReference<AnvilChunk>>     cachedChunks;
    private final LongSet                                   gcLongSet;

    /**
     * New cache for Anvil format Chunks
     * @param world for which we cache chunks
     */
	public AnvilChunkCache( final AnvilWorldAdapter world ) {
		this.world = world;
		this.cachedChunks = HashLongObjMaps.newMutableMap();
        this.gcLongSet = HashLongSets.newMutableSet();
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

        this.gcLongSet.clear();

        int spawnAreaSize = this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea();

        // Check for gc
        synchronized ( this.cachedChunks ) {
            for ( SoftReference<AnvilChunk> chunkSoftReference : this.cachedChunks.values() ) {
                AnvilChunk chunk = chunkSoftReference.get();

                // Check if this chunk is whitelisted
                if ( chunk == null || this.gcLongSet.contains( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ) ) ) {
                    continue;
                }

                // Check if this chunk is part of the spawn
                if ( this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea() > 0 ) {
                    if ( chunk.getX() <= spawnXChunk + spawnAreaSize &&
                            chunk.getZ() <= spawnZChunk + spawnAreaSize ) {
                        // Get the biggest viewDistance in this chunk
                        int viewDistance = detectViewDistance( chunk );

                        // Whitelist all chunks which are in the viewdistance of this
                        if ( viewDistance > 0 ) {
                            for ( int whitelistX = chunk.getX() - viewDistance; whitelistX < chunk.getX() + viewDistance; whitelistX++ ) {
                                for ( int whitelistZ = chunk.getZ() - viewDistance; whitelistZ < chunk.getZ() + viewDistance; whitelistZ++ ) {
                                    this.gcLongSet.add( CoordinateUtils.toLong( whitelistX, whitelistZ ) );
                                }
                            }
                        } else {
                            // Always keep this chunk
                            this.gcLongSet.add( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ) );
                        }
                    }
                }

                // Check if this chunk wants to be gced
                if ( !chunk.canBeGCed() ) {
                    // Get the biggest viewDistance in this chunk
                    int viewDistance = detectViewDistance( chunk );

                    // Whitelist all chunks which are in the viewdistance of this
                    if ( viewDistance > 0 ) {
                        for ( int whitelistX = chunk.getX() - viewDistance; whitelistX < chunk.getX() + viewDistance; whitelistX++ ) {
                            for ( int whitelistZ = chunk.getZ() - viewDistance; whitelistZ < chunk.getZ() + viewDistance; whitelistZ++ ) {
                                this.gcLongSet.add( CoordinateUtils.toLong( whitelistX, whitelistZ ) );
                            }
                        }
                    }
                }
            }

            // Remove all chunks which are not whitelisted
            this.cachedChunks.removeIf( new LongObjPredicate<SoftReference<AnvilChunk>>() {
                @Override
                public boolean test( long key, SoftReference<AnvilChunk> anvilChunkSoftReference ) {
                    return !gcLongSet.contains( key ) && anvilChunkSoftReference.get() != null;
                }
            } );
        }
    }

    private int detectViewDistance( ChunkAdapter chunk ) {
        int viewDistance = 0;

        for ( EntityPlayer entityPlayer : chunk.getPlayers() ) {
            if ( entityPlayer.getViewDistance() > viewDistance ) {
                viewDistance = entityPlayer.getViewDistance();
                if ( viewDistance >= this.world.getServer().getServerConfig().getViewDistance() ) {
                    viewDistance = this.world.getServer().getServerConfig().getViewDistance();
                    break;
                }
            }
        }

        return viewDistance;
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
