package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 177 )
public class ItemWallBanner extends ItemBanner {

    // CHECKSTYLE:OFF
    public ItemWallBanner( short data, int amount ) {
        super( 177, data, amount );
    }

    public ItemWallBanner( short data, int amount, NBTTagCompound nbt ) {
        super( 177, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
