package io.gomint.plugin;

import io.gomint.i18n.LocaleManager;
import io.gomint.plugin.listener.PlayerInteractListener;
import io.gomint.plugin.listener.PlayerJoinListener;
import io.gomint.plugin.listener.PlayerRespawnListener;
import io.gomint.plugin.scoreboard.DebugScoreboard;
import io.gomint.world.generator.CreateOptions;
import io.gomint.world.generator.integrated.LayeredGenerator;
import lombok.Getter;

import java.util.Locale;

/**
 * @author geNAZt
 * @version 1.0
 */
@PluginName( "TestPlugin" )
@Version( major = 1, minor = 0 )
@Startup( StartupPriority.STARTUP )
public class TestPlugin extends Plugin {

    @Getter
    private DebugScoreboard scoreboard;

    @Override
    public void onInstall() {
        // Generate scoreboard
        this.scoreboard = new DebugScoreboard( this );

        // Register listener
        registerListener( new PlayerJoinListener( this ) );
        registerListener( new PlayerInteractListener() );
        registerListener( new PlayerRespawnListener() );
    }

}
