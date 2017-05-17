package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketCraftingEvent extends Packet {

    private byte windowId;
    private int recipeType;
    private UUID recipeId;
    private List<ItemStack> input;
    private List<ItemStack> output;

    public PacketCraftingEvent() {
        super( Protocol.PACKET_CRAFTING_EVENT );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {

    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.windowId = buffer.readByte();
        this.recipeType = buffer.readSignedVarInt();
        this.recipeId = buffer.readUUID();
        this.input = readItemStacks( buffer );
        this.output = readItemStacks( buffer );
    }

}
