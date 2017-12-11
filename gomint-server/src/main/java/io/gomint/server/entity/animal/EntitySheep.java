/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.animal;

import io.gomint.server.entity.EntityLiving;
import io.gomint.server.entity.EntityType;
import io.gomint.server.world.WorldAdapter;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EntitySheep extends EntityLiving {

    /**
     * Constructs a new EntityLiving
     *
     * @param type  The type of the Entity
     * @param world The world in which this entity is in
     */
    public EntitySheep( EntityType type, WorldAdapter world ) {
        super( type, world );
    }

}
