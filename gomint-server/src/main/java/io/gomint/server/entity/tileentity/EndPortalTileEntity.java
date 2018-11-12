package io.gomint.server.entity.tileentity;

import io.gomint.server.inventory.InventoryHolder;
import io.gomint.server.world.block.Block;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EndPortalTileEntity extends ContainerTileEntity implements InventoryHolder {

    /**
     * Construct new tile entity
     *
     * @param block of the tile entity
     */
    public EndPortalTileEntity( Block block ) {
        super( block );
    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "EndPortal" );
    }

}
