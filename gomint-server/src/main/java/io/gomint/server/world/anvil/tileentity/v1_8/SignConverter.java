/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.SignTileEntity;
import io.gomint.server.world.anvil.tileentity.TileEntityConverter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SignConverter extends TileEntityConverter<SignTileEntity> {

    @Override
    public SignTileEntity readFrom( NBTTagCompound compound ) {
        return null;
    }

    @Override
    public void writeTo( SignTileEntity entity, NBTTagCompound compound ) {

    }

}
