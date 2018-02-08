/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.v1_8;

import io.gomint.math.Location;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.anvil.tileentity.TileEntityConverter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 * @param <T> type of tile entity which this converter should generate
 */
public abstract class BasisConverter<T> extends TileEntityConverter<T> {

    /**
     * Construct new converter
     *
     * @param worldAdapter for which we construct
     */
    public BasisConverter( WorldAdapter worldAdapter ) {
        super( worldAdapter );
    }

    /**
     * Read a position from the compound given
     *
     * @param compound which contains x, y and z position integers
     * @return block position object
     */
    protected Location getPosition( NBTTagCompound compound ) {
        return new Location(
            this.worldAdapter,
            compound.getInteger( "x", 0 ),
            compound.getInteger( "y", -1 ),
            compound.getInteger( "z", 0 )
        );
    }

}
