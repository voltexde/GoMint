package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemSword;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 272 )
 public class ItemStoneSword extends ItemReduceTierStone implements io.gomint.inventory.item.ItemStoneSword, ItemSword {

    // CHECKSTYLE:OFF
    public ItemStoneSword( short data, int amount ) {
        super( 272, data, amount );
    }

    public ItemStoneSword( short data, int amount, NBTTagCompound nbt ) {
        super( 272, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
