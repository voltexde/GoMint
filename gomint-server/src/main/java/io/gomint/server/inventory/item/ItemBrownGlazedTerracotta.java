package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 232 )
public class ItemBrownGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemBrownGlazedTerracotta( short data, int amount ) {
        super( 232, data, amount );
    }

    public ItemBrownGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 232, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
