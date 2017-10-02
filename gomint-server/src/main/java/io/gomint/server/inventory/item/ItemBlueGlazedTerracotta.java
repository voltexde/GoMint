package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 231 )
public class ItemBlueGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemBlueGlazedTerracotta( short data, int amount ) {
        super( 231, data, amount );
    }

    public ItemBlueGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 231, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
