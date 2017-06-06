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
public class PacketContainerSetSlot extends Packet {

    private byte windowId;
    private int slot;
    private int hotbarSlot;
    private ItemStack itemStack;
    private byte selectedSlot;

    public PacketContainerSetSlot() {
        super( Protocol.PACKET_CONTAINER_SET_SLOT );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeByte( this.windowId );
        buffer.writeSignedVarInt( this.slot );
        buffer.writeSignedVarInt( this.hotbarSlot );
        writeItemStack( this.itemStack, buffer );
        buffer.writeByte( this.selectedSlot );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.windowId = buffer.readByte();
        this.slot = buffer.readSignedVarInt();
        this.hotbarSlot = buffer.readSignedVarInt();
        this.itemStack = readItemStack( buffer );
        this.selectedSlot = buffer.readByte();
    }

    @Override
    public int estimateLength() {
        return 1 + predictSignedVarInt( this.slot ) + predictSignedVarInt( this.hotbarSlot ) + predictItemStack( this.itemStack ) + 1;
    }

}
