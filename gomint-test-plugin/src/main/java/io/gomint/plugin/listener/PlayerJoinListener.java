package io.gomint.plugin.listener;

import io.gomint.entity.passive.EntityHuman;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.EventPriority;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.math.Vector;
import io.gomint.player.PlayerSkin;
import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements EventListener {

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onPlayerJoin( PlayerJoinEvent event ) {
        // Set to allow all permissions
        event.getPlayer().getPermissionManager().setPermission( "*", true );

        // Spawn a entity human in front
        EntityHuman entityHuman = EntityHuman.create();
        entityHuman.setSkin( PlayerSkin.empty() );
        entityHuman.spawn( event.getPlayer().getSpawnLocation().add( new Vector( 2, 0, 2 ) ) );
    }

}
