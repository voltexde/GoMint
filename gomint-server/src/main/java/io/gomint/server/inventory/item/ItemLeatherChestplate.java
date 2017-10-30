package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 299 )
public class ItemLeatherChestplate extends ItemArmor implements io.gomint.inventory.item.ItemLeatherTunic {

    // CHECKSTYLE:OFF
    public ItemLeatherChestplate( short data, int amount ) {
        super( 299, data, amount );
    }

    public ItemLeatherChestplate( short data, int amount, NBTTagCompound nbt ) {
        super( 299, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public float getReductionValue() {
        return 3;
    }

}
