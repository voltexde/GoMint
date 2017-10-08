package io.gomint.server.util.collection;

import com.koloboke.compile.KolobokeMap;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
public abstract class GeneratorAPIClassMap<R> {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static <V> GeneratorAPIClassMap<V> withExpectedSize( int expectedSize ) {
        return new KolobokeGeneratorAPIClassMap<V>( expectedSize );
    }

    public abstract int getOrDefault(R key, int def);
    public abstract int put(R key, int val);

}
