package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.network.Protocol;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.taglib.NBTWriter;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketTileEntityData extends Packet {

    private Vector position;
    private TileEntity tileEntity;

    public PacketTileEntityData() {
        super( Protocol.PACKET_TILE_ENTITY_DATA );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        // Block position
        buffer.writeSignedVarInt( (int) this.position.getX() );
        buffer.writeUnsignedVarInt( (int) this.position.getY() );
        buffer.writeSignedVarInt( (int) this.position.getZ() );

        // NBT Tag
        NBTTagCompound compound = new NBTTagCompound( "" );
        this.tileEntity.toCompound( compound );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTWriter nbtWriter = new NBTWriter( baos, ByteOrder.LITTLE_ENDIAN );
        nbtWriter.setUseVarint( true );
        try {
            nbtWriter.write( compound );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        buffer.writeBytes( baos.toByteArray() );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {

    }

}
