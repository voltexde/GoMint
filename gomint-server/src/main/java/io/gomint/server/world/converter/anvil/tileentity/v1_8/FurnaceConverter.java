/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.FurnaceTileEntity;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.springframework.context.ApplicationContext;

/**
 * @author geNAZt
 * @version 1.0
 */
public class FurnaceConverter extends BasisConverter<FurnaceTileEntity> {

    public FurnaceConverter( ApplicationContext context, Object2IntMap<String> itemConverter ) {
        super( context, itemConverter );
    }

    @Override
    public FurnaceTileEntity readFrom( NBTTagCompound compound ) {
        compound.addValue( "BurnDuration", compound.getShort( "CookTimeTotal", (short) 0 ) );
        FurnaceTileEntity tileEntity = new FurnaceTileEntity( getBlock( compound ) );
        this.context.getAutowireCapableBeanFactory().autowireBean( tileEntity );
        tileEntity.fromCompound( compound );
        return tileEntity;
    }

}
