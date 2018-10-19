package io.gomint.plugin.listener;

import io.gomint.GoMint;
import io.gomint.entity.passive.EntityFallingBlock;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.EventPriority;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.plugin.TestPlugin;
import io.gomint.world.Gamemode;
import io.gomint.world.block.BlockCraftingTable;
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
        event.getPlayer().setGamemode( Gamemode.CREATIVE );
        event.getPlayer().setPlayerListName("§bTest §7| §b" + event.getPlayer().getName());
    }

}
