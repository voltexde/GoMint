package io.gomint.plugin;

import io.gomint.GoMint;
import io.gomint.entity.passive.EntityHuman;
import io.gomint.math.Location;
import io.gomint.player.PlayerSkin;
import io.gomint.plugin.config.Config;
import io.gomint.plugin.generator.PlotChunkGenerator;
import io.gomint.plugin.listener.BlockBreakListener;
import io.gomint.plugin.listener.EntityTeleportListener;
import io.gomint.plugin.listener.PlayerJoinListener;
import io.gomint.plugin.listener.PlayerMoveListener;
import io.gomint.world.generator.CreateOptions;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

/**
 * @author geNAZt
 * @version 1.0
 */
@PluginName( "TestPlugin" )
@Version( major = 1, minor = 0 )
@Startup( StartupPriority.STARTUP )
public class TestPlugin extends Plugin {

    @Getter
    private Config config;

    @Override
    public void onStartup() {
        getLogger().info( "Started " + this.getName() + " " + this.getVersion() );

        // Check if the plugin folder is there
        File pluginDirectory = this.getDataFolder();
        if ( !pluginDirectory.exists() ) {
            pluginDirectory.mkdirs();
        }

        // Load config
        this.config = new Config();
        try {
            this.config.initialize( new File( pluginDirectory, "config.cfg" ) );
        } catch ( IOException e ) {
            throw new PluginLoadException( "Could not init config", e );
        }

        // Check if we need to test chunk generation
        if ( this.config.isEnableChunkGeneration() ) {
            GoMint.instance().createWorld( "test_plot", new CreateOptions().generator( PlotChunkGenerator.class ) );
        }
    }

    @Override
    public void onInstall() {
        EntityHuman floatingText = EntityHuman.create();
        floatingText.setSkin( PlayerSkin.empty() );
        floatingText.setScale( 0f );
        floatingText.setTicking( false );
        floatingText.setNameTag( "Test123" );
        floatingText.spawn( new Location( GoMint.instance().getWorld( "test_plot" ), 12, 83, 12 ) );

        // Register listener
        registerListener( new PlayerMoveListener() );
        registerListener( new PlayerJoinListener() );
        registerListener( new BlockBreakListener() );
        registerListener( new EntityTeleportListener() );
    }

    @Override
    public void onUninstall() {

    }

}
