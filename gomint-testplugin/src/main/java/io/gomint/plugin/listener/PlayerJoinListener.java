package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.plugin.TestPlugin;
import io.gomint.world.Gamemode;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements EventListener {

    private final TestPlugin testPlugin;

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        this.testPlugin.getScheduler().schedule( new Runnable() {
            @Override
            public void run() {
                event.getPlayer().teleport( testPlugin.getServer().getWorld( "world1" ).getSpawnLocation() );
                event.getPlayer().setGamemode( Gamemode.SPECTATOR );
            }
        }, 5, TimeUnit.SECONDS );
    }

}
