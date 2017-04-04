package io.gomint.server.network.packet;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.Vector;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketUseItem extends Packet {

    private Vector position;

    private int interactBlockId;
    private int face;

    private Vector facePosition;
    private Vector playerPosition;

    private int slot;
    private ItemStack item;

    public PacketUseItem() {
        super( Protocol.PACKET_USE_ITEM );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeSignedVarInt( (int) this.position.getX() );
        buffer.writeUnsignedVarInt( (int) this.position.getY() );
        buffer.writeSignedVarInt( (int) this.position.getZ() );

        buffer.writeUnsignedVarInt( this.interactBlockId );
        buffer.writeSignedVarInt( this.face );

        buffer.writeLFloat( this.facePosition.getX() );
        buffer.writeLFloat( this.facePosition.getY() );
        buffer.writeLFloat( this.facePosition.getZ() );

        buffer.writeLFloat( this.playerPosition.getX() );
        buffer.writeLFloat( this.playerPosition.getY() );
        buffer.writeLFloat( this.playerPosition.getZ() );

        buffer.writeSignedVarInt( this.slot );
        writeItemStack( this.item, buffer );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.position = new Vector( buffer.readSignedVarInt(), buffer.readUnsignedVarInt(), buffer.readSignedVarInt() );
        this.interactBlockId = buffer.readUnsignedVarInt();
        this.face = buffer.readSignedVarInt();
        this.facePosition = new Vector( buffer.readLFloat(), buffer.readLFloat(), buffer.readLFloat() );
        this.playerPosition = new Vector( buffer.readLFloat(), buffer.readLFloat(), buffer.readLFloat() );
        this.slot = buffer.readSignedVarInt();
        this.item = readItemStack( buffer );
    }

}
