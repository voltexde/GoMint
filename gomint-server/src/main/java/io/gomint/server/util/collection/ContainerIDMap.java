package io.gomint.server.util.collection;

import com.koloboke.compile.KolobokeMap;
import io.gomint.server.inventory.ContainerInventory;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
public abstract class ContainerIDMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static ContainerIDMap withExpectedSize( int expectedSize ) {
        return new KolobokeContainerIDMap( expectedSize );
    }

    public abstract boolean justRemove( ContainerInventory containerInventory );
    public abstract byte getByte( ContainerInventory id );
    public abstract void justPut( ContainerInventory containerInventory, byte id );

}
