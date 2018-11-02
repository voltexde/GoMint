/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.entity;

import io.gomint.server.entity.Entity;
import io.gomint.taglib.NBTTagCompound;

/**
 * @param <T> type of entity
 * @author geNAZt
 * @version 1.0
 */
public interface EntityConverter<T extends Entity> {

    /**
     * Construct and read a entity from the given compound
     *
     * @param compound which should be read
     * @return entity with config found in the compound
     */
    T readFrom( NBTTagCompound compound );

}
