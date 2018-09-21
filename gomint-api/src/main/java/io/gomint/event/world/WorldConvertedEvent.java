/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.event.world;

import io.gomint.event.Event;
import io.gomint.world.World;

/**
 * @author geNAZt
 * @version 1.0
 */
public class WorldConvertedEvent extends Event {

    private final World world;

    /**
     * Create a new world converted event. This is fired when a anvil world has been converted to leveldb.
     *
     * @param world which has been converted
     */
    public WorldConvertedEvent( World world ) {
        this.world = world;
    }

    /**
     * Get the world which has been converted
     *
     * @return the converted world
     */
    public World getWorld() {
        return this.world;
    }

}
