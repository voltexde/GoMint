package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketMobEquipment extends Packet {

    private long entityId;

    private ItemStack stack;
    private byte slot;
    private byte selectedSlot;

    public PacketMobEquipment() {
        super( Protocol.PACKET_MOB_EQUIPMENT );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeLong( this.entityId );
        writeItemStack( this.stack, buffer );
        buffer.writeByte( this.slot );
        buffer.writeByte( this.selectedSlot );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.entityId = buffer.readLong();
        this.stack = readItemStack( buffer );
        this.slot = buffer.readByte();
        this.selectedSlot = buffer.readByte();
    }

}
