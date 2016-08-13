package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Getter;

import javax.xml.bind.DatatypeConverter;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketPlayerAction extends Packet {

    private long entityId;

    private PlayerAction action;

    private int x;
    private int y;
    private int z;

    private int face;

    // There is more data but who knows what that could be

    public PacketPlayerAction() {
        super( Protocol.PACKET_PLAYER_ACTION );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {

    }

    @Override
    public void deserialize( PacketBuffer buffer ) {
        this.entityId = buffer.readLong();
        this.action = PlayerAction.getAction( buffer.readInt() );
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.face = buffer.readInt();
    }

    public enum PlayerAction {
        START_BREAK( 1 ),
        JUMP( 8 );

        @Getter
        private final int actionId;

        PlayerAction( int actionId ) {
            this.actionId = actionId;
        }

        public static PlayerAction getAction( int actionId ) {
            for ( PlayerAction playerAction : values() ) {
                if ( playerAction.actionId == actionId ) {
                    return playerAction;
                }
            }

            System.out.println( "Unknown action id: " + actionId );
            return null;
        }
    }

}
