package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketAnimate extends Packet {

    private int actionId;
    private long entityId;

    public PacketAnimate() {
        super( Protocol.PACKET_ANIMATE );
    }

    @Override
    public void serialize( PacketBuffer buffer, int protocolID ) {
        buffer.writeSignedVarInt( this.actionId );
        buffer.writeUnsignedVarLong( this.entityId );
    }

    @Override
    public void deserialize( PacketBuffer buffer, int protocolID ) {
        this.actionId = buffer.readSignedVarInt();
        this.entityId = buffer.readUnsignedVarLong();
    }

}
