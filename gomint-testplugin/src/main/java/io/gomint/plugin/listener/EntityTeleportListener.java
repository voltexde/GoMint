/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.plugin.listener;

import io.gomint.entity.EntityPlayer;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.entity.EntityTeleportEvent;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EntityTeleportListener implements EventListener {

    @EventHandler
    public void onEntityTeleport( EntityTeleportEvent event ) {
        if ( event.getEntity() instanceof EntityPlayer ) {
            ( (EntityPlayer) event.getEntity() ).getInventory().clear();
        }
    }

}
