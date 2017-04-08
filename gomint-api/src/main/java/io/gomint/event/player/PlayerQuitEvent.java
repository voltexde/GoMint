package io.gomint.event.player;

import io.gomint.entity.Player;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerQuitEvent extends PlayerEvent {

    /**
     * Construct a quit event with the player who disconnected
     *
     * @param player The player which disconnected
     */
    public PlayerQuitEvent( Player player ) {
        super( player );
    }

}
