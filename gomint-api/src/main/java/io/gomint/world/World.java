/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world;

import io.gomint.math.Location;
import io.gomint.math.Vector;

/**
 * @author geNAZt
 * @author BlackyPaw
 * @version 1.0
 */
public interface World {

	/**
	 * Gets the name of the world itself, i.e. the name of the directory it is stored in.
	 *
	 * @return The name of the world itself
	 */
	String getWorldName();

    /**
     * Gets the level name of the world. This is the name found inside the NBT data of a world.
     *
     * @return The level name of the world
     */
    String getLevelName();

	/**
	 * Gets the spawn location of the world.
	 *
	 * @return The world's spawn location
	 */
	Location getSpawnLocation();

    /**
     * Get the block at that position or null if the position is not loaded in the world.
     *
     * @param vector of the position
     * @return block which has been found at that position or null
     */
    Block getBlockAt( Vector vector );

	/**
	 * Gets the value of the given gamerule set for this world.
	 *
	 * @param gamerule The gamerule to get a value for
	 * @param <T> The generic type of the gamerule
	 * @return The value of the gamerule
	 */
    <T> T getGamerule( Gamerule<T> gamerule );
}
