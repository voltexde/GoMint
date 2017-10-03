package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.BlockPosition;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.tileentity.ChestTileEntity;
import io.gomint.server.entity.tileentity.ShulkerBoxTileEntity;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 */
@RegisterInfo( id = 218 )
public class ShulkerBox extends Block {

    @Override
    public int getBlockId() {
        return 218;
    }

    @Override
    public long getBreakTime() {
        return 9000;
    }

    @Override
    public boolean needsTileEntity() {
        return true;
    }

    @Override
    public boolean interact( Entity entity, int face, Vector facePos, ItemStack item ) {
        ShulkerBoxTileEntity tileEntity = this.getTileEntity();
        if ( tileEntity != null ) {
            tileEntity.interact( entity, face, facePos, item );
            return true;
        }

        return false;
    }

    @Override
    public TileEntity createTileEntity() {
        BlockPosition position = this.location.toBlockPosition();

        NBTTagCompound compound = new NBTTagCompound( "" );

        // Add generic tile entity stuff
        compound.addValue( "x", position.getX() );
        compound.addValue( "y", position.getY() );
        compound.addValue( "z", position.getZ() );

        // Add flags
        compound.addValue( "isUndyed", (byte) 1 );
        compound.addValue( "facing", (byte) 1 );

        return new ShulkerBoxTileEntity( compound, this.world );
    }

}
