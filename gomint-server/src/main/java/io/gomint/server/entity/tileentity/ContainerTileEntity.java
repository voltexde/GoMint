package io.gomint.server.entity.tileentity;

import io.gomint.server.world.block.Block;
import io.gomint.taglib.NBTTagCompound;
import lombok.Getter;
import lombok.Setter;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ContainerTileEntity extends TileEntity {

    @Getter
    @Setter
    private String customName = null;

    /**
     * Construct new tile entity
     *
     * @param block of the tile entity
     */
    ContainerTileEntity( Block block ) {
        super( block );
    }

    @Override
    public void fromCompound( NBTTagCompound compound ) {
        super.fromCompound( compound );

        this.customName = compound.getString( "CustomName", null );
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        if ( this.customName != null ) {
            compound.addValue( "CustomName", this.customName );
        }
    }

}
