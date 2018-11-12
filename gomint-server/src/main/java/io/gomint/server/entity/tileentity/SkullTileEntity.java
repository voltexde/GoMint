package io.gomint.server.entity.tileentity;

import io.gomint.math.MojangRotation;
import io.gomint.server.world.block.Block;
import io.gomint.taglib.NBTTagCompound;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class SkullTileEntity extends TileEntity {

    private MojangRotation rotation;
    private byte skullType;

    /**
     * Construct a new skull tile based on given data
     *
     * @param block which holds this tile
     */
    public SkullTileEntity( Block block ) {
        super( block );
    }

    @Override
    public void fromCompound( NBTTagCompound compound ) {
        super.fromCompound( compound );

        this.rotation = new MojangRotation( compound.getByte( "Rot", (byte) 0 ) );
        this.skullType = compound.getByte( "SkullType", (byte) 0 );
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "Skull" );
        compound.addValue( "Rot", this.rotation.getRotationValue() );
        compound.addValue( "SkullType", this.skullType );
    }

}
