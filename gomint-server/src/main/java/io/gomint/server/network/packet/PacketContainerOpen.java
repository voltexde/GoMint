package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.BlockPosition;
import io.gomint.math.Vector;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketContainerOpen extends Packet {

    private byte windowId;
    private byte type;
    private BlockPosition location;
    private long entityId;

    /**
     * Construct a new container open packet
     */
    public PacketContainerOpen() {
        super( Protocol.PACKET_CONTAINER_OPEN );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {

    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.windowId = buffer.readByte();
        this.type = buffer.readByte();
        this.location = readBlockPosition( buffer );
        this.entityId = buffer.readSignedVarLong().longValue();
    }

}
