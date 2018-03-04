/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util.collection;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;
import io.gomint.server.util.Pair;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class PaletteBlockMap {

    /**
     * Create a new map for chunk caches
     *
     * @param expectedSize for the new map
     * @return a new map which uses long as key and ChunkAdapter values
     */
    public static PaletteBlockMap withExpectedSize( int expectedSize ) {
        return new KolobokePaletteBlockMap( expectedSize );
    }

    public abstract void justPut(int key, Pair<Byte, Byte> value);
    public abstract Pair<Byte, Byte> get(int key);

}
