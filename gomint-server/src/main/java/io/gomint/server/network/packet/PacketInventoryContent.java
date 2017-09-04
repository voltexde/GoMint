package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 */
@Data
public class PacketInventoryContent extends Packet {

    private int windowId;
    private ItemStack[] items;

    public PacketInventoryContent() {
        super( Protocol.PACKET_INVENTORY_CONTENT_PACKET  );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeUnsignedVarInt( this.windowId );
        writeItemStacks( this.items, buffer );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {

    }
}
