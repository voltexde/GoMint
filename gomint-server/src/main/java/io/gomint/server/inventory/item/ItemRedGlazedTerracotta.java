package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 234 )
public class ItemRedGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemRedGlazedTerracotta( short data, int amount ) {
        super( 234, data, amount );
    }

    public ItemRedGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 234, data, amount, nbt );
    }
    // CHECKSTYLE:ON
    
}
