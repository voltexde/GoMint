/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.entity.v1_8;

import io.gomint.server.entity.passive.EntityVillager;
import io.gomint.server.inventory.item.Items;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EntityVillagerConverter extends EntityConverter<EntityVillager> {

    public EntityVillagerConverter( Items items, Object2IntMap<String> itemConverter ) {
        super( items, itemConverter );
    }

    @Override
    public EntityVillager readFrom( NBTTagCompound compound ) {
        EntityVillager villager = super.readFrom( compound );

        // Get profession
        int profession = compound.getInteger( "Profession", 0 );
        switch ( profession ) {
            case 0: // Farmer
                villager.setProfession( io.gomint.entity.passive.EntityVillager.Profession.FARMER );
                break;
            case 1: // Librarian
                villager.setProfession( io.gomint.entity.passive.EntityVillager.Profession.LIBRARIAN );
                break;
            case 2: // Priest
                villager.setProfession( io.gomint.entity.passive.EntityVillager.Profession.PRIEST );
                break;
            case 3: // Blacksmith
                villager.setProfession( io.gomint.entity.passive.EntityVillager.Profession.BLACKSMITH );
                break;
            case 4: // Butcher
                villager.setProfession( io.gomint.entity.passive.EntityVillager.Profession.BUTCHER );
                break;
            default: // All others get redirected to farmer profession
                villager.setProfession( io.gomint.entity.passive.EntityVillager.Profession.FARMER );
                break;
        }

        return villager;
    }

    @Override
    public EntityVillager createEntity() {
        return new EntityVillager();
    }

}
