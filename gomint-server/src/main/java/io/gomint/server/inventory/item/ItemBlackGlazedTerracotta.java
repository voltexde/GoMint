package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 235 )
public class ItemBlackGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemBlackGlazedTerracotta( short data, int amount ) {
        super( 235, data, amount );
    }

    public ItemBlackGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 235, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
