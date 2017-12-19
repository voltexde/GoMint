package io.gomint.server.inventory;

import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.entity.tileentity.EnchantTableTileEntity;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.type.WindowType;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnchantmentTableInventory extends ContainerInventory {

    /**
     * Construct a new container inventory
     *
     * @param owner of the container (mostly a tile or normal entity)
     */
    public EnchantmentTableInventory( InventoryHolder owner ) {
        super( owner, 1 );
    }

    @Override
    public WindowType getType() {
        return WindowType.ENCHANTMENT;
    }

    @Override
    public void onOpen( EntityPlayer player ) {

    }

    @Override
    public void onClose( EntityPlayer player ) {

    }

    @Override
    public void sendContents( PlayerConnection playerConnection ) {

    }

    @Override
    public void sendContents( int slot, PlayerConnection playerConnection ) {

    }

    public void setTileEntity( EnchantTableTileEntity tileEntity ) {
        this.owner = tileEntity;
    }

}
