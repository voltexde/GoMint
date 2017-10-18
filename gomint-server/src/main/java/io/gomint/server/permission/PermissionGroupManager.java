/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.permission;

import io.gomint.permission.Group;
import io.gomint.permission.GroupManager;
import io.gomint.server.util.collection.PermissionGroupMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PermissionGroupManager implements GroupManager {

    private PermissionGroupMap groupMap = null;

    @Override
    public Group getOrCreateGroup( String name ) {
        // Check if this is the first group we get/create
        if ( this.groupMap == null ) {
            this.groupMap = PermissionGroupMap.withExpectedSize( 10 );

            PermissionGroup group = new PermissionGroup( name );
            this.groupMap.justPut( name, group );
            return group;
        }

        Group group = this.groupMap.get( name );
        if ( group == null ) {
            group = new PermissionGroup( name );
            this.groupMap.justPut( name, group );
        }

        return group;
    }

    @Override
    public void removeGroup( Group group ) {
        this.groupMap.justRemove( group.getName() );
    }

}
