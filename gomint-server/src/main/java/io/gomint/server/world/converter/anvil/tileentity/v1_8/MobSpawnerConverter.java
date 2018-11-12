/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.MobSpawnerTileEntity;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.springframework.context.ApplicationContext;

/**
 * @author geNAZt
 * @version 1.0
 */
public class MobSpawnerConverter extends BasisConverter<MobSpawnerTileEntity> {

    private final Object2IntMap<String> entityConverter;

    public MobSpawnerConverter( ApplicationContext context, Object2IntMap<String> itemConverter, Object2IntMap<String> entityConverter ) {
        super( context, itemConverter );
        this.entityConverter = entityConverter;
    }

    @Override
    public MobSpawnerTileEntity readFrom( NBTTagCompound compound ) {
        // Convert EntityId
        String saveId = compound.getString( "EntityId", "Chicken" );
        int networkId = this.entityConverter.getInt( saveId );
        compound.remove( "EntityId" );
        compound.addValue( "EntityId", networkId );

        MobSpawnerTileEntity tileEntity = new MobSpawnerTileEntity( getBlock( compound ) );
        this.context.getAutowireCapableBeanFactory().autowireBean( tileEntity );
        tileEntity.fromCompound( compound );
        return tileEntity;
    }

}
