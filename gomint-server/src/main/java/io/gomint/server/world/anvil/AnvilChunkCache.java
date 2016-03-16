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
import net.openhft.koloboke.collect.map.LongObjCursor;
import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class AnvilChunkCache extends ChunkCacheAdapter {
    private static final Logger logger = LoggerFactory.getLogger( AnvilChunkCache.class );

	// ==================================== FIELDS ==================================== //
	private final AnvilWorldAdapter         world;
	private final LongObjMap<AnvilChunk>    cachedChunks;

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
            return this.cachedChunks.get( CoordinateUtils.toLong( x, z ) );
        }
	}

    @Override
    public void tick() {
        int spawnXChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getX() );
        int spawnZChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getZ() );

        LongSet skipChunks = HashLongSets.newMutableSet();

        int spawnAreaSize = this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea();

        // Check for gc
        synchronized ( this.cachedChunks ) {
            LongObjCursor<AnvilChunk> cursor = this.cachedChunks.cursor();
            while ( cursor.moveNext() ) {
                // Check if this chunk is whitelisted
                if ( skipChunks.contains( cursor.key() ) ) {
                    continue;
                }

                // Check if this chunk is part of the spawn
                if ( this.world.getServer().getServerConfig().getAmountOfChunksForSpawnArea() > 0 ) {
                    if ( cursor.value().getX() <= spawnXChunk + spawnAreaSize &&
                            cursor.value().getZ() <= spawnZChunk + spawnAreaSize ) {
                        // Get the biggest viewDistance in this chunk
                        int viewDistance = detectViewDistance( cursor.value() );

                        // Whitelist all chunks which are in the viewdistance of this
                        if ( viewDistance > 0 ) {
                            for ( int whitelistX = cursor.value().getX() - viewDistance; whitelistX < cursor.value().getX() + viewDistance; whitelistX++ ) {
                                for ( int whitelistZ = cursor.value().getZ() - viewDistance; whitelistZ < cursor.value().getZ() + viewDistance; whitelistZ++ ) {
                                    skipChunks.add( CoordinateUtils.toLong( whitelistX, whitelistZ ) );
                                }
                            }
                        } else {
                            // Always keep this chunk
                            skipChunks.add( CoordinateUtils.toLong( cursor.value().getX(), cursor.value().getZ() ) );
                        }
                    }
                }

                // Check if this chunk wants to be gced
                if ( !cursor.value().canBeGCed() ) {
                    // Get the biggest viewDistance in this chunk
                    int viewDistance = detectViewDistance( cursor.value() );

                    // Whitelist all chunks which are in the viewdistance of this
                    if ( viewDistance > 0 ) {
                        for ( int whitelistX = cursor.value().getX() - viewDistance; whitelistX < cursor.value().getX() + viewDistance; whitelistX++ ) {
                            for ( int whitelistZ = cursor.value().getZ() - viewDistance; whitelistZ < cursor.value().getZ() + viewDistance; whitelistZ++ ) {
                                skipChunks.add( CoordinateUtils.toLong( whitelistX, whitelistZ ) );
                            }
                        }
                    }
                }
            }

            // Remove all chunks which are not whitelisted
            cursor = this.cachedChunks.cursor();
            while ( cursor.moveNext() ) {
                if ( !skipChunks.contains( cursor.key() ) ) {
                    cursor.remove();
                }
            }
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
            this.cachedChunks.put( CoordinateUtils.toLong( chunk.getX(), chunk.getZ() ), chunk );
        }
	}
}
