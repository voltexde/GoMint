package io.gomint.server.entity.tileentity;

import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SkullTileEntity extends TileEntity {

    private float rotation;
    private byte skullType;

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    public SkullTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );

        this.rotation = tagCompound.getByte( "Rot", (byte) 0 ) * 22.5f;
        this.skullType = tagCompound.getByte( "SkullType", (byte) 0 );
    }

    @Override
    public void update( long currentMillis, float dF ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound ) {
        super.toCompound( compound );

        compound.addValue( "id", "Skull" );
        compound.addValue( "Rot", (byte) ( (int) ( Math.floor( ( ( this.rotation ) ) * 16 / 360 ) + 0.5 ) & 0x0f ) );
        compound.addValue( "SkullType", this.skullType );
    }

}
