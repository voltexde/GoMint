package io.gomint.server.util.collection;

import com.koloboke.collect.LongCursor;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeSet;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeSet
@ConcurrentModificationUnchecked
public abstract class GUIDSet {

    /**
     * Create a new set for invalid GUIDs
     *
     * @param expectedSize for the new set
     * @return a new set which holds GUIDs for connection removing
     */
    public static GUIDSet withExpectedSize( int expectedSize ) {
        return new KolobokeGUIDSet( expectedSize );
    }

    public abstract boolean isEmpty();
    public abstract LongCursor cursor();
    public abstract void clear();
    public abstract boolean add(long guid);

}
