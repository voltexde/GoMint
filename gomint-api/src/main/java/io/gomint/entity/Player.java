/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.entity;

import io.gomint.math.Location;
import io.gomint.player.PlayerSkin;
import io.gomint.world.Gamemode;

import java.util.UUID;

/**
 * @author BlackyPaw
 * @author Digot
 * @version 1.0
 */
public interface Player extends Entity {

    /**
     * Gets the name of the player. It is NOT globally unique since the
     * player can change it inside the Client. Yet it is unique for all
     * players on the same server.
     *
     * @return The name player's name
     */
    String getName();

    /**
     * Gets the player's UUID. It has yet to be researched how unique this
     * one is as it may be specified by the player during the login sequence.
     *
     * @return The player's UUID.
     */
    UUID getUUID();

    /**
     * Set the new gamemode for this player
     *
     * @param gamemode The new gamemode to be used
     */
    void setGamemode( Gamemode gamemode );

    /**
     * Get the current gamemode
     *
     * @return The gamemode the player is in
     */
    Gamemode getGamemode();

    /**
     * Is this player op'ed?
     *
     * @return true when op'ed, false when not
     */
    boolean isOp();

    /**
     * Hide another player from this player
     *
     * @param player The player which should be hidden
     */
    void hidePlayer( Player player );

    /**
     * Show a hidden player again
     *
     * @param player The player which should be shown again
     */
    void showPlayer( Player player );

    /**
     * Check if given player is hidden to this player
     *
     * @param player The player which should be checked for
     * @return true if this player can't see the player given, false it it can
     */
    boolean isHidden( Player player );

    /**
     * Get the skin of a player. This is readonly access currently since we figure out how to change the skin.
     *
     * @return skin which the client has sent on login
     */
    PlayerSkin getSkin();

    /**
     * Teleport to the given location
     *
     * @param to The location where the player should be teleported to
     */
    void teleport( Location to );

}
