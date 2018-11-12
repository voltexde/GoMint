/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.math.MojangRotation;
import io.gomint.server.entity.tileentity.SkullTileEntity;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.springframework.context.ApplicationContext;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SkullConverter extends BasisConverter<SkullTileEntity> {

    public SkullConverter( ApplicationContext context, Object2IntMap<String> itemConverter ) {
        super( context, itemConverter );
    }

    @Override
    public SkullTileEntity readFrom( NBTTagCompound compound ) {
        SkullTileEntity tileEntity = new SkullTileEntity( getBlock( compound ) );
        this.context.getAutowireCapableBeanFactory().autowireBean( tileEntity );

        // We only need the skull type and rotation
        byte rotation = compound.getByte( "Rot", (byte) 0 );
        byte skullType = compound.getByte( "SkullType", (byte) 0 );

        tileEntity.setRotation( new MojangRotation( rotation ) );
        tileEntity.setSkullType( skullType );

        return tileEntity;
    }

}
