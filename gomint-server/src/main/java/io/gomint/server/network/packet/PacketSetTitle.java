package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Getter;
import lombok.Setter;

public class PacketSetTitle extends Packet {

    public static final int TYPE_CLEAR = 0;
    public static final int TYPE_RESET = 1;
    public static final int TYPE_TITLE = 2;
    public static final int TYPE_SUBTITLE = 3;
    public static final int TYPE_ACTION_BAR = 4;
    public static final int TYPE_ANIMATION_TIMES = 5;

    @Getter@Setter
    public int type;
    @Getter@Setter
    public String text = "";
    @Getter@Setter
    public int fadeInTime = 0;
    @Getter@Setter
    public int stayTime = 0;
    @Getter@Setter
    public int fadeOutTime = 0;

    public PacketSetTitle() {
        super( Protocol.PACKET_SET_TITLE );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeSignedVarInt( type );
        buffer.writeString( text );
        buffer.writeSignedVarInt( fadeInTime );
        buffer.writeSignedVarInt( stayTime);
        buffer.writeSignedVarInt( fadeOutTime );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.type = buffer.readSignedVarInt();
        this.text = buffer.readString();
        this.fadeInTime = buffer.readSignedVarInt();
        this.stayTime = buffer.readSignedVarInt();
        this.fadeOutTime = buffer.readSignedVarInt();
    }
}
