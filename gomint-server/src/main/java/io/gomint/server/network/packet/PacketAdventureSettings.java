package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketAdventureSettings extends Packet {
    private int flags;
    private int userPermission;

    public PacketAdventureSettings() {
        super( Protocol.PACKET_ADVENTURE_SETTINGS );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeUnsignedVarInt( this.flags );
        buffer.writeUnsignedVarInt( this.userPermission );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.flags = buffer.readUnsignedVarInt();
        this.userPermission = buffer.readUnsignedVarInt();
    }

}
