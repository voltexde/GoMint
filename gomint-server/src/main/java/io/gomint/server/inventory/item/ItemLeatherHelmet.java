package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 298 )
public class ItemLeatherHelmet extends ItemArmor implements io.gomint.inventory.item.ItemLeatherHelmet {

    // CHECKSTYLE:OFF
    public ItemLeatherHelmet( short data, int amount ) {
        super( 298, data, amount );
    }

    public ItemLeatherHelmet( short data, int amount, NBTTagCompound nbt ) {
        super( 298, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public float getReductionValue() {
        return 1;
    }

}
