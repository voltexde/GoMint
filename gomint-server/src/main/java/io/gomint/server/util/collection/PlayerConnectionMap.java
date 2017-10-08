package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;
import com.koloboke.function.LongObjConsumer;
import io.gomint.server.network.PlayerConnection;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class PlayerConnectionMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static PlayerConnectionMap withExpectedSize( int expectedSize ) {
        return new KolobokePlayerConnectionMap( expectedSize );
    }

    public abstract void justPut( long key, PlayerConnection value );
    public abstract PlayerConnection remove( long key );
    public abstract void forEach( LongObjConsumer<? super PlayerConnection> c );


}
