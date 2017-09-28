package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemReduceBreaktime;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ItemReduceTierWooden extends ItemStack implements ItemReduceBreaktime {

    // CHECKSTYLE:OFF
    ItemReduceTierWooden( int material, short data, int amount ) {
        super( material, data, amount );
    }

    ItemReduceTierWooden( int material, short data, int amount, NBTTagCompound nbt ) {
        super( material, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public double getDivisor() {
        return 2;
    }

}
