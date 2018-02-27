/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;
import com.koloboke.function.LongLongConsumer;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class WatchdogMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static WatchdogMap withExpectedSize( int expectedSize ) {
        return new KolobokeWatchdogMap( expectedSize );
    }

    public abstract void justPut( long threadId, long endtime );
    public abstract boolean justRemove( long threadId );
    public abstract void forEach( LongLongConsumer consumer );

}
