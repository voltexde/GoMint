package io.gomint.plugin.listener;

import io.gomint.GoMint;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.EventPriority;
import io.gomint.event.entity.EntityDamageEvent;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.plugin.TestPlugin;
import io.gomint.scoreboard.DisplaySlot;
import io.gomint.scoreboard.Scoreboard;
import io.gomint.scoreboard.ScoreboardDisplay;
import io.gomint.scoreboard.SortOrder;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

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

        Scoreboard scoreboard = GoMint.instance().createScoreboard();
        ScoreboardDisplay display = scoreboard.addDisplay( DisplaySlot.SIDEBAR, "test", "TEST", SortOrder.DESCENDING );

        display.addLine( "test1", 0 );
        display.addLine( "test2", 1 );

        event.getPlayer().setScoreboard( scoreboard );

        this.plugin.getScheduler().schedule( () -> event.getPlayer().attack( 25, EntityDamageEvent.DamageSource.VOID ), 1, TimeUnit.SECONDS );
    }

}
