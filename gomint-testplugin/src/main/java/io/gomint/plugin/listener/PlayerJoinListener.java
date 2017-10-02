package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.inventory.item.ItemAcaciaDoor;
import io.gomint.inventory.item.ItemWoodPlanks;
import io.gomint.inventory.item.ItemWoodenAxe;
import io.gomint.world.Gamemode;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerJoinListener implements EventListener {

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        event.getPlayer().getInventory().setItem( 0, ItemWoodPlanks.create( 12 ) );
        event.getPlayer().getInventory().setItem( 1, ItemAcaciaDoor.create( 1 ) );
        event.getPlayer().getInventory().setItem( 2, ItemWoodenAxe.create( 1 ) );

        event.getPlayer().setGamemode( Gamemode.CREATIVE );
    }

}
