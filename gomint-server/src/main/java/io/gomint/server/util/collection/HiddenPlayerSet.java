package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeSet;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeSet
@ConcurrentModificationUnchecked
public abstract class HiddenPlayerSet {

    /**
     * Create a new set for invalid GUIDs
     *
     * @param expectedSize for the new set
     * @return a new set which holds GUIDs for connection removing
     */
    public static HiddenPlayerSet withExpectedSize( int expectedSize ) {
        return new KolobokeHiddenPlayerSet( expectedSize );
    }

    public abstract void clear();
    public abstract boolean add(long hash);
    public abstract boolean removeLong(long hash);
    public abstract boolean contains(long hash);

}
