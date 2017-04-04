package io.gomint.plugin;

import io.gomint.event.CancelableEvent;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ListenerHandler {

    public ListenerHandler( TestPlugin plugin ) {
        plugin.getScheduler().executeAsync( new Runnable() {
            @Override
            public void run() {
                plugin.registerListener( new EventListener() {
                    @EventHandler
                    public void onPlayerJoin( CancelableEvent e ) {

                    }
                } );
            }
        } );
    }

}
