/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.ChestTileEntity;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ChestConverter extends BasisConverter<ChestTileEntity> {

    /**
     * Construct new converter
     *
     * @param worldAdapter for which we construct
     */
    public ChestConverter( WorldAdapter worldAdapter ) {
        super( worldAdapter );
    }

    @Override
    public ChestTileEntity readFrom( NBTTagCompound compound ) {
        return null;
    }

    @Override
    public void writeTo( ChestTileEntity entity, NBTTagCompound compound ) {

    }

}
