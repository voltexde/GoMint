package io.gomint.plugin;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@Name( "TestPlugin" )
@Version( major = 1, minor = 0 )
public class TestPlugin extends Plugin {

    @Override
    public void onStartup() {
        getLogger().info( "Started " + this.getName() + " " + this.getVersion() );

        getScheduler().schedule( new Runnable() {
            @Override
            public void run() {
                getLogger().info( "Running a runnable" );
            }
        }, 100, 100, TimeUnit.MILLISECONDS );
    }

    @Override
    public void onInstall() {
        new ListenerHandler( this );
    }

    @Override
    public void onUninstall() {
        // This is like Bukkits onDisable();
    }

}
