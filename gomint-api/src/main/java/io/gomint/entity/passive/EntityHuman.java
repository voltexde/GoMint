/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.entity.passive;

import io.gomint.GoMint;
import io.gomint.entity.EntityCreature;
import io.gomint.player.PlayerSkin;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface EntityHuman extends EntityCreature {

    /**
     * Create a new entity human with no config
     *
     * @return empty, fresh human
     */
    static EntityHuman create() {
        return GoMint.instance().createEntity( EntityHuman.class );
    }

    /**
     * Get the skin of a player. This is readonly access currently since we figure out how to change the skin.
     *
     * @return skin which the client has sent on login
     */
    PlayerSkin getSkin();

    /**
     * Set the skin of this human
     *
     * @param skin which should be set
     */
    void setSkin( PlayerSkin skin );

}
