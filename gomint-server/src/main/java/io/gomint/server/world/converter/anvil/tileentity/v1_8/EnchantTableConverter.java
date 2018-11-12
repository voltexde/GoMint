/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.EnchantTableTileEntity;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.springframework.context.ApplicationContext;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnchantTableConverter extends BasisConverter<EnchantTableTileEntity> {

    public EnchantTableConverter( ApplicationContext context, Object2IntMap<String> itemConverter ) {
        super( context, itemConverter );
    }

    @Override
    public EnchantTableTileEntity readFrom( NBTTagCompound compound ) {
        EnchantTableTileEntity tileEntity = new EnchantTableTileEntity( getBlock( compound ) );
        this.context.getAutowireCapableBeanFactory().autowireBean( tileEntity );
        tileEntity.fromCompound( compound );
        return tileEntity;
    }

}
