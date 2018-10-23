package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.EventPriority;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.inventory.item.ItemDiamondSword;
import io.gomint.inventory.item.ItemDye;
import io.gomint.inventory.item.data.DyeType;
import io.gomint.plugin.TestPlugin;
import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements EventListener {

    private final TestPlugin plugin;

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onPlayerJoin( PlayerJoinEvent event ) {
        // Set to allow all permissions
        event.getPlayer().getPermissionManager().setPermission( "*", true );
        event.getPlayer().setLevel( 45 );
        event.getPlayer().getInventory().setItem( 0, ItemDiamondSword.create( 1 ) );

        ItemDye dye = ItemDye.create( 64 );
        dye.setDyeType( DyeType.LAPIS_LAZULI );
        event.getPlayer().getInventory().setItem( 1, dye );
    }

}
