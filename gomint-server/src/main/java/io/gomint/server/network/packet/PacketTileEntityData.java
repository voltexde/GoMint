package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.BlockPosition;
import io.gomint.server.network.Protocol;
import io.gomint.taglib.NBTReaderNoBuffer;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.taglib.NBTWriter;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteOrder;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketTileEntityData extends Packet {

    private static final int MAX_ALLOC = 1024 * 1024;

    private BlockPosition position;
    private NBTTagCompound compound;

    public PacketTileEntityData() {
        super( Protocol.PACKET_TILE_ENTITY_DATA );
    }

    @Override
    public void serialize( PacketBuffer buffer, int protocolID ) throws Exception {
        // Block position
        writeBlockPosition( this.position, buffer );

        // NBT Tag
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTWriter nbtWriter = new NBTWriter( baos, ByteOrder.LITTLE_ENDIAN );
        nbtWriter.setUseVarint( true );
        nbtWriter.write( this.compound );

        buffer.writeBytes( baos.toByteArray() );
    }

    @Override
    public void deserialize( PacketBuffer buffer, int protocolID ) throws Exception {
        this.position = readBlockPosition( buffer );

        byte[] data = new byte[buffer.getRemaining()];
        buffer.readBytes( data );

        NBTReaderNoBuffer reader = new NBTReaderNoBuffer( new ByteArrayInputStream( data ), ByteOrder.LITTLE_ENDIAN );
        reader.setUseVarint( true );
        reader.setAllocateLimit( MAX_ALLOC );

        this.compound = reader.parse();
    }

}
