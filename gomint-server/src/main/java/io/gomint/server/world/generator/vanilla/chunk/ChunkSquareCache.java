/*
 * Copyright (c) 2018 Gomint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.generator.vanilla.chunk;

import io.gomint.server.world.CoordinateUtils;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ChunkSquareCache {

    private final LongObjectMap<ChunkSquare> cache = new LongObjectHashMap<>();

    /**
     * Get the correct square for the given chunk coordinates
     *
     * @param x coordinate of the chunk
     * @param z coordinate of the chunk
     * @return the cache square in which the chunk resides
     */
    public ChunkSquare getChunkSquare( int x, int z ) {
        int iX = x >> 4;
        int iZ = z >> 4;

        long key = CoordinateUtils.toLong( iX, iZ );

        synchronized ( this.cache ) {
            ChunkSquare chunkSquare = this.cache.get( key );
            if ( chunkSquare != null ) {
                return chunkSquare;
            }

            ChunkSquare returnVal = new ChunkSquare( iX, iZ );
            this.cache.put( key, returnVal );
            return returnVal;
        }
    }

}
