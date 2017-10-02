package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 223 )
public class ItemLightBlueGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemLightBlueGlazedTerracotta( short data, int amount ) {
        super( 223, data, amount );
    }

    public ItemLightBlueGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 223, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
