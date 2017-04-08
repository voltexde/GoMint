package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerMoveEvent;
import io.gomint.world.Gamemode;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerMoveListener implements EventListener {

    @EventHandler
    public void onPlayerMove( PlayerMoveEvent event ) {
        if ( event.getPlayer().getGamemode() != Gamemode.SPECTATOR ) {
            event.setCancelled( true );
        }
    }

}
