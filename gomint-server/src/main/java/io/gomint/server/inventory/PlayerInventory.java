package io.gomint.server.inventory;

import io.gomint.server.entity.EntityPlayer;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerInventory extends MobInventory {

    public PlayerInventory( EntityPlayer player ) {
        super( 36 );
    }

    @Override
    protected void sendContents( int index ) {

    }

    @Override
    protected void sendArmor( int slot ) {

    }

}
