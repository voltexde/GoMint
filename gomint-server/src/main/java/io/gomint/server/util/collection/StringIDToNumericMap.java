package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class StringIDToNumericMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static StringIDToNumericMap withExpectedSize( int expectedSize ) {
        return new KolobokeStringIDToNumericMap( expectedSize );
    }

    public abstract void justPut(String key, int value);
    public abstract int getOrDefault(String key, int def);

}
