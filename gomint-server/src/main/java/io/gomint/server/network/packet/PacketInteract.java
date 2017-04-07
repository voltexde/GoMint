package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketInteract extends Packet {

    public enum InteractAction {
        INTERACT,
        ATTACK,
        MOUSEOVER;

        public static InteractAction valueOf( byte actionId ) {
            switch ( actionId ) {
                case 1:
                    return INTERACT;
                case 2:
                    return ATTACK;
                case 4:
                    return MOUSEOVER;
                default:
                    return MOUSEOVER;
            }
        }

        public static byte getId( InteractAction action ) {
            switch ( action ) {
                case INTERACT:
                    return 1;
                case ATTACK:
                    return 2;
                case MOUSEOVER:
                    return 4;
            }

            return 4;
        }
    }

    private InteractAction action;
    private long entityId;

    public PacketInteract() {
        super( Protocol.PACKET_INTERACT );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeByte( InteractAction.getId( this.action ) );
        buffer.writeUnsignedVarLong( this.entityId );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.action = InteractAction.valueOf( buffer.readByte() );
        this.entityId = buffer.readUnsignedVarLong();
    }

}
