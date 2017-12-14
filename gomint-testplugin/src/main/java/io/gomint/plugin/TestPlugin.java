package io.gomint.plugin;

import io.gomint.GoMint;
import io.gomint.entity.passive.EntityXPOrb;
import io.gomint.math.Location;
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

        // Register listener
        registerListener( new PlayerMoveListener() );
        registerListener( new PlayerJoinListener( this ) );
    }

    @Override
    public void onUninstall() {
        // This is like Bukkits onDisable();
    }

}
