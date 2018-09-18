/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity;

import io.gomint.taglib.NBTTagCompound;
import lombok.AllArgsConstructor;

/**
 * @param <T> type of tile entity which this converter should generate
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
public abstract class TileEntityConverter<T> {

    /**
     * Construct and read a tile entity from the given compound
     *
     * @param compound which should be read
     * @return entity with config found in the compound
     */
    public abstract T readFrom( NBTTagCompound compound );

}
