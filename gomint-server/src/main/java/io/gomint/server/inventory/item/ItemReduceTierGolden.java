package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemReduceBreaktime;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ItemReduceTierGolden extends ItemStack implements ItemReduceBreaktime {

    // CHECKSTYLE:OFF
    ItemReduceTierGolden( int material, short data, int amount ) {
        super( material, data, amount );
    }

    ItemReduceTierGolden( int material, short data, int amount, NBTTagCompound nbt ) {
        super( material, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public double getDivisor() {
        return 16;
    }

}
