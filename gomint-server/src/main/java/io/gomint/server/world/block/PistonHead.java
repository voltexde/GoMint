package io.gomint.server.world.block;

import io.gomint.math.BlockPosition;
import io.gomint.server.entity.tileentity.PistonArmTileEntity;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 34 )
public class PistonHead extends Block {

    @Override
    public int getBlockId() {
        return 34;
    }

    @Override
    public long getBreakTime() {
        return 750;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean needsTileEntity() {
        return true;
    }

    @Override
    TileEntity createTileEntity( NBTTagCompound compound ) {
        if ( compound == null ) {
            compound = new NBTTagCompound( "" );
        }

        BlockPosition position = this.location.toBlockPosition();

        // Add generic tile entity stuff
        compound.addValue( "x", position.getX() );
        compound.addValue( "y", position.getY() );
        compound.addValue( "z", position.getZ() );

        return new PistonArmTileEntity( compound, this.world );
    }

}
