package io.gomint.server.inventory;

import io.gomint.server.network.PlayerConnection;

/**
 * @author geNAZt
 * @version 1.0
 */
public class CraftingResultInventory extends Inventory {

    public CraftingResultInventory( InventoryHolder owner ) {
        super( owner, 4 );
    }

    @Override
    public void sendContents( PlayerConnection playerConnection ) {

    }

    @Override
    public void sendContents( int slot, PlayerConnection playerConnection ) {

    }

}
