package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 176 )
public class ItemStandingBanner extends ItemBanner {

    // CHECKSTYLE:OFF
    public ItemStandingBanner( short data, int amount ) {
        super( 176, data, amount );
    }

    public ItemStandingBanner( short data, int amount, NBTTagCompound nbt ) {
        super( 176, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
