package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;

import java.util.Collection;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class ChunkSliceIndexMap<R> {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static <V> ChunkSliceIndexMap<V> withExpectedSize( int expectedSize ) {
        return new KolobokeChunkSliceIndexMap<V>( expectedSize );
    }

    public abstract R get(short key);
    public abstract Collection<R> values();
    public abstract void justPut(short key, R value);
    public abstract boolean justRemove(short key);

}
