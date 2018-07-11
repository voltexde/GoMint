package io.gomint.plugin;

import io.gomint.plugin.listener.EntityDamageListener;
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
        registerListener( new PlayerJoinListener() );
        registerListener( new EntityDamageListener() );
    }

    @Override
    public void onUninstall() {

    }

}
