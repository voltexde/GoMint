package io.gomint.plugin;

import io.gomint.GoMint;
import io.gomint.entity.passive.EntityHuman;
import io.gomint.entity.passive.EntityXPOrb;
import io.gomint.math.Location;
import io.gomint.player.PlayerSkin;
import io.gomint.plugin.listener.PlayerJoinListener;
import io.gomint.plugin.listener.PlayerMoveListener;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@PluginName( "TestPlugin" )
@Version( major = 1, minor = 0 )
public class TestPlugin extends Plugin {

    @Getter
    private EntityXPOrb bossBarOrb;

    @Override
    public void onStartup() {
        getLogger().info( "Started " + this.getName() + " " + this.getVersion() );
    }

    @Override
    public void onInstall() {
        // Generate boss bar orb
        this.bossBarOrb = EntityXPOrb.create();
        this.bossBarOrb.setPickupDelay( 3650, TimeUnit.DAYS );
        this.bossBarOrb.setTicking( false );
        this.bossBarOrb.setNameTagAlwaysVisible( false );
        this.bossBarOrb.setNameTagVisible( true );
        this.bossBarOrb.setNameTag( "GoMint Bossbar Test" );
        this.bossBarOrb.spawn( new Location( GoMint.instance().getWorld( "Skywars" ), 0, -10, 0 ) );

        // Spawn NPC
        EntityHuman npc = EntityHuman.create();
        npc.setSkin( PlayerSkin.fromURL( "http://puu.sh/yHz9d/1dee829ba6.png" ) );
        npc.setDisplayName( "Test" );
        npc.spawn( new Location( GoMint.instance().getWorld( "Skywars" ), 5, 65, 2 ) );

        // Spawn floating text
        EntityHuman floatingText = EntityHuman.create();
        floatingText.setSkin( PlayerSkin.empty() );
        floatingText.setScale( 0f );
        floatingText.setTicking( false );
        floatingText.setNameTag( "Test floating text" );
        floatingText.spawn( new Location( GoMint.instance().getWorld( "Skywars" ), 5, 71, 2 ) );

        // Register listener
        registerListener( new PlayerMoveListener() );
        registerListener( new PlayerJoinListener( this ) );
    }

    @Override
    public void onUninstall() {
        // This is like Bukkits onDisable();
    }

}
