/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.permission;

import com.koloboke.collect.map.ObjObjCursor;
import io.gomint.permission.Group;
import io.gomint.server.util.collection.PermissionMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PermissionGroup implements Group {

    private String name;
    private PermissionMap permissions;

    /**
     * Create a new permission group. This needs to be configured via
     *
     * @param name of the group
     */
    PermissionGroup( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setPermission( String permission, boolean value ) {
        if ( this.permissions == null ) {
            this.permissions = PermissionMap.withExpectedSize( 50 );
        }

        this.permissions.justPut( permission.intern(), value );
    }

    @Override
    public void removePermission( String permission ) {
        if ( this.permissions != null ) {
            this.permissions.justRemove( permission.intern() );
        }
    }

    @Override
    public ObjObjCursor<String, Boolean> cursor() {
        if ( this.permissions == null ) {
            return null;
        }

        return this.permissions.cursor();
    }

    public Boolean get( String permission ) {
        return this.permissions.get( permission );
    }

}
