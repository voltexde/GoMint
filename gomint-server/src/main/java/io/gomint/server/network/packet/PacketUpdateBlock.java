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
public class PacketUpdateBlock extends Packet {

    private Vector position;
    private int blockId;
    private int prioAndMetadata;

    public PacketUpdateBlock() {
        super( Protocol.PACKET_UPDATE_BLOCK );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeSignedVarInt( (int) this.position.getX() );
        buffer.writeUnsignedVarInt( (int) this.position.getY() );
        buffer.writeSignedVarInt( (int) this.position.getZ() );

        buffer.writeUnsignedVarInt( this.blockId );
        buffer.writeUnsignedVarInt( this.prioAndMetadata );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {

    }

}
