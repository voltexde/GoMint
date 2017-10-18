package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.world.ChunkAdapter;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class PlayerMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static PlayerMap withExpectedSize( int expectedSize ) {
        return new KolobokePlayerMap( expectedSize );
    }

    public abstract Collection<EntityPlayer> keySet();
    public abstract ChunkAdapter remove(EntityPlayer player);
    public abstract ChunkAdapter get(EntityPlayer player);
    public abstract void justPut(EntityPlayer player, ChunkAdapter adapter);
    public abstract int size();
    public abstract void forEach(BiConsumer<? super EntityPlayer, ? super ChunkAdapter> action);

}
