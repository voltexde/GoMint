package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.inventory.item.ItemFlintAndSteel;
import io.gomint.inventory.item.ItemTNT;
import io.gomint.inventory.item.ItemWritableBook;
import io.gomint.plugin.TestPlugin;
import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements EventListener {

    private final TestPlugin plugin;

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        event.getPlayer().setDisplayName( "§6Project Lead §7|§6 " + event.getPlayer().getName() );
        // event.getPlayer().teleport( GoMint.instance().getDefaultWorld().getSpawnLocation().clone().add( 0, 1, 0 ) );

        event.getPlayer().getInventory().setItem( 0, ItemWritableBook.create( 1 ) );

        event.getPlayer().getInventory().setItem( 1, ItemTNT.create( 64 ) );
        event.getPlayer().getInventory().setItem( 2, ItemFlintAndSteel.create( 1 ) );
    }

}
