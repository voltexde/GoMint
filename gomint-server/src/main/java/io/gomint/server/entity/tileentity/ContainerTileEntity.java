package io.gomint.server.entity.tileentity;

import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 */
public class ContainerTileEntity extends TileEntity {

    private String customName = null;

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    ContainerTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );

        this.customName = tagCompound.getString( "CustomName", null );
    }

    /**
     * Return the custom name of this container
     *
     * @return custom name or null when none is set
     */
    public String getCustomName() {
        return this.customName;
    }

    @Override
    public void update( long currentMillis, float dF ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound ) {
        super.toCompound( compound );

        if ( this.customName != null ) {
            compound.addValue( "CustomName", this.customName );
        }
    }

}
