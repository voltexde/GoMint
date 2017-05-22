package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.Vector;
import io.gomint.server.network.Protocol;
import io.gomint.server.world.SoundMagicNumbers;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketWorldSoundEvent extends Packet {

    private SoundMagicNumbers type;
    private Vector position;
    private int extraData = -1;
    private int pitch = -1;
    private boolean unknownBool;
    private boolean unknownBool2;

    public PacketWorldSoundEvent() {
        super( Protocol.PACKET_WORLD_SOUND_EVENT );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeByte( this.type.getSoundId() );
        buffer.writeLFloat( (float) this.position.getX() );
        buffer.writeLFloat( (float) this.position.getY() );
        buffer.writeLFloat( (float) this.position.getZ() );
        buffer.writeSignedVarInt( this.extraData );
        buffer.writeSignedVarInt( this.pitch );
        buffer.writeBoolean( this.unknownBool );
        buffer.writeBoolean( this.unknownBool2 );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.type = SoundMagicNumbers.valueOf( buffer.readByte() );
        this.position = new Vector( buffer.readLFloat(), buffer.readLFloat(), buffer.readLFloat() );
        this.extraData = buffer.readSignedVarInt();
        this.pitch = buffer.readSignedVarInt();
        this.unknownBool = buffer.readBoolean();
        this.unknownBool2 = buffer.readBoolean();
    }

    @Override
    public int estimateLength() {
        return 1 + 4 + 4 + 4 + predictSignedVarInt( this.extraData ) + predictSignedVarInt( this.pitch ) + 2;
    }
}
