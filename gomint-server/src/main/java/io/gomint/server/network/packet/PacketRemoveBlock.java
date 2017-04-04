package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.Vector;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketRemoveBlock extends Packet {

    private Vector position;

    public PacketRemoveBlock() {
        super( Protocol.PACKET_REMOVE_BLOCK );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeSignedVarInt( (int) this.position.getX() );
        buffer.writeUnsignedVarInt( (int) this.position.getY() );
        buffer.writeSignedVarInt( (int) this.position.getZ() );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.position = new Vector( buffer.readSignedVarInt(), buffer.readUnsignedVarInt(), buffer.readSignedVarInt() );
    }

}
