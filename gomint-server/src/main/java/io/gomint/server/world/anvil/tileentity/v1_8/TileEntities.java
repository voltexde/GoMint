/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.world.anvil.tileentity.TileEntityConverter;
import io.gomint.server.world.anvil.tileentity.TileEntityConverters;
import io.gomint.taglib.NBTTagCompound;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TileEntities implements TileEntityConverters {

    @Getter
    enum Tiles {
        SIGN( "Sign", SignConverter.class );

        private final String id;
        private final Class<? extends TileEntityConverter> converterClass;

        Tiles( String id, Class<? extends TileEntityConverter> converterClass ) {
            this.id = id;
            this.converterClass = converterClass;
        }
    }

    @Override
    public TileEntity read( NBTTagCompound compound ) {
        return null;
    }

    @Override
    public NBTTagCompound write( TileEntity tileEntity ) {
        return null;
    }

}
