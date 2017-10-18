package io.gomint.server.world.block;

import io.gomint.math.BlockPosition;
import io.gomint.server.entity.tileentity.ItemFrameTileEntity;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 199 )
public class ItemFrame extends Block {

    @Override
    public int getBlockId() {
        return 199;
    }

    @Override
    public boolean needsTileEntity() {
        return true;
    }

    @Override
    TileEntity createTileEntity( NBTTagCompound compound ) {
        BlockPosition position = this.location.toBlockPosition();

        compound = new NBTTagCompound( "" );

        // Add generic tile entity stuff
        compound.addValue( "x", position.getX() );
        compound.addValue( "y", position.getY() );
        compound.addValue( "z", position.getZ() );

        // Set item
        NBTTagCompound item = new NBTTagCompound( "Item" );
        item.addValue( "id", (short) 0 );
        item.addValue( "Damage", (short) 0 );
        item.addValue( "Count", (byte) 0 );
        compound.addValue( "Item", item );
        compound.addValue( "ItemDropChance", 1.0f );
        compound.addValue( "ItemRotation", (byte) 0 );

        return new ItemFrameTileEntity( compound, this.world );
    }

}
