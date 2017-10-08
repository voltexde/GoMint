package io.gomint.server.util.collection;

import com.koloboke.collect.set.IntSet;
import com.koloboke.compile.KolobokeMap;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
public abstract class GeneratorMap<R> {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static <V> GeneratorMap<V> withExpectedSize( int expectedSize ) {
        return new KolobokeGeneratorMap<V>( expectedSize );
    }

    public abstract IntSet keySet();
    public abstract R get(int key);
    public abstract R put(int key, R val);

}
