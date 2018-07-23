package io.gomint.plugin;

import io.gomint.plugin.listener.EntityDamageListener;
import io.gomint.plugin.listener.PlayerInteractListener;
import io.gomint.plugin.listener.PlayerJoinListener;

/**
 * @author geNAZt
 * @version 1.0
 */
@PluginName( "TestPlugin" )
@Version( major = 1, minor = 0 )
@Startup( StartupPriority.STARTUP )
public class TestPlugin extends Plugin {

    @Override
    public void onInstall() {
        // Register listener
        registerListener( new PlayerJoinListener( this ) );
        registerListener( new EntityDamageListener() );
        registerListener( new PlayerInteractListener() );
    }

    @Override
    public void onUninstall() {

    }

}
