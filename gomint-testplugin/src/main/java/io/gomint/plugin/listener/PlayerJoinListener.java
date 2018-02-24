package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.inventory.item.ItemArrow;
import io.gomint.inventory.item.ItemBow;
import io.gomint.world.Gamemode;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerJoinListener implements EventListener {

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        // event.getPlayer().setGamemode( Gamemode.CREATIVE );

        event.getPlayer().getInventory().setItem( 0, ItemBow.create( 1 ) );
        event.getPlayer().getInventory().setItem( 1, ItemArrow.create( 64 ) );
    }

}
