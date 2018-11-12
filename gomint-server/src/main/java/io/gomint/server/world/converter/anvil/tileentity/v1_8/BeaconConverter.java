/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.BeaconTileEntity;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.springframework.context.ApplicationContext;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BeaconConverter extends BasisConverter<BeaconTileEntity> {

    public BeaconConverter( ApplicationContext context, Object2IntMap<String> itemConverter ) {
        super( context, itemConverter );
    }

    @Override
    public BeaconTileEntity readFrom( NBTTagCompound compound ) {

        int primary = compound.getInteger( "Primary", 0 );
        int secondary = compound.getInteger( "Secondary", 0 );

        BeaconTileEntity tileEntity = new BeaconTileEntity( getBlock( compound ) );
        this.context.getAutowireCapableBeanFactory().autowireBean( tileEntity );
        tileEntity.setPrimary( primary );
        tileEntity.setSecondary( secondary );
        return tileEntity;
    }

}
