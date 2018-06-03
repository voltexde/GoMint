/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerPreJoinEvent;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerPreJoinListener implements EventListener {

    @EventHandler
    public void onPlayerPreJoin( PlayerPreJoinEvent event ) {
        event.getPlayer().setSpawnLocation( event.getPlayer().getWorld().getSpawnLocation().add( 100, 0, 100 ) );
    }

}
