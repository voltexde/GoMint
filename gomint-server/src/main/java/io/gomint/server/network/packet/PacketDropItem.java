package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketDropItem extends Packet {

    private byte itemType;
    private ItemStack itemStack;

    public PacketDropItem() {
        super( Protocol.PACKET_DROP_ITEM );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {

    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.itemType = buffer.readByte();
        this.itemStack = readItemStack( buffer );
    }

}
