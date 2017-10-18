/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util.collection;

import com.koloboke.collect.map.ObjObjCursor;
import com.koloboke.compile.KolobokeMap;

/**
 * @author geNAZt
 * @version 1.0
 */
@KolobokeMap
public abstract class PermissionMap {

    /**
     * Create a new map for permissions
     *
     * @param expectedSize for the new map
     * @return a new map which uses string as key and boolean as values
     */
    public static PermissionMap withExpectedSize( int expectedSize ) {
        return new KolobokePermissionMap( expectedSize );
    }

    // CHECKSTYLE:OFF
    public abstract void justPut( String permission, Boolean value );
    public abstract boolean justRemove( String permission );
    public abstract ObjObjCursor<String, Boolean> cursor();
    public abstract void clear();
    public abstract Boolean get( String permission );
    // CHECKSTYLE:ON

}
