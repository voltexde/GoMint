package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketContainerSetContent extends Packet {

    private byte windowId;
    private ItemStack[] slots;
    private int[] hotbar;

    public PacketContainerSetContent() {
        super( Protocol.PACKET_CONTAINER_SET_CONTENT );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeByte( this.windowId );
        writeItemStacks( this.slots, buffer, this.windowId != 0x76 );
        writeIntList( this.hotbar, buffer );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {

    }

}
