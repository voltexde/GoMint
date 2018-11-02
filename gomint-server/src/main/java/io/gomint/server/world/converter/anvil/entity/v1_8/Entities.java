/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.entity.v1_8;

import io.gomint.server.entity.Entity;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.world.converter.anvil.entity.EntityConverters;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Entities implements EntityConverters {

    private final EntityVillagerConverter villagerConverter;

    public Entities( Items items, Object2IntMap<String> itemConverter ) {
        this.villagerConverter = new EntityVillagerConverter( items, itemConverter );
    }

    @Override
    public Entity read( NBTTagCompound compound ) {
        String id = compound.getString( "id", null );
        if ( id != null ) {
            switch ( id ) {
                case "Villager":
                    return this.villagerConverter.readFrom( compound );
            }
        }

        return null;
    }

}
