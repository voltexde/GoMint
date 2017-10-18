package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;
import com.koloboke.function.ByteObjConsumer;
import io.gomint.server.entity.metadata.MetadataValue;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class MetadataMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static MetadataMap withExpectedSize( int expectedSize ) {
        return new KolobokeMetadataMap( expectedSize );
    }

    // CHECKSTYLE:OFF
    public abstract boolean containsKey( byte key );
    public abstract void justPut( byte key, MetadataValue value );
    public abstract MetadataValue get( byte key );
    public abstract int size();
    public abstract void forEach( ByteObjConsumer<? super MetadataValue> c );
    public abstract void clear();
    // CHECKSTYLE:ON

}
