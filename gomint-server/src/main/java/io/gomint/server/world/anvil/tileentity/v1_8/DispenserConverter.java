/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.DispenserTileEntity;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class DispenserConverter extends BasisConverter<DispenserTileEntity> {

    /**
     * Construct new converter
     *
     * @param worldAdapter for which we construct
     */
    public DispenserConverter( WorldAdapter worldAdapter ) {
        super( worldAdapter );
    }

    @Override
    public DispenserTileEntity readFrom( NBTTagCompound compound ) {
        return null;
    }

    @Override
    public void writeTo( DispenserTileEntity entity, NBTTagCompound compound ) {

    }

}
