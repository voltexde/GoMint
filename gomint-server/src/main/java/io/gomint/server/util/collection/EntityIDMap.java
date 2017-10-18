package io.gomint.server.util.collection;

import com.koloboke.collect.ObjCollection;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;
import io.gomint.entity.Entity;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class EntityIDMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static EntityIDMap withExpectedSize( int expectedSize ) {
        return new KolobokeEntityIDMap( expectedSize );
    }

    // CHECKSTYLE:OFF
    public abstract Entity get(long key);
    public abstract ObjCollection<Entity> values();
    public abstract boolean containsKey(long key);
    public abstract void justPut(long key, Entity entity);
    public abstract boolean justRemove(long key);
    public abstract int size();
    public abstract LongObjCursor<Entity> cursor();
    public abstract void clear();
    // CHECKSTYLE:ON

}
