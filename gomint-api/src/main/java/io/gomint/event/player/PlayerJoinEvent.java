package io.gomint.event.player;

import io.gomint.entity.Player;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerJoinEvent extends PlayerEvent {

    public PlayerJoinEvent( Player player ) {
        super( player );
    }

}
