package io.gomint.server.network.handler;

import io.gomint.entity.Player;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.PlayerConnectionState;
import io.gomint.server.network.packet.PacketPlayerlist;
import io.gomint.server.network.packet.PacketResourcePackResponse;
import io.gomint.server.network.packet.PacketResourcePackStack;
import io.gomint.server.network.packet.PacketSpawnPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketResourcePackResponseHandler implements PacketHandler<PacketResourcePackResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger( PacketResourcePackResponseHandler.class );

    @Override
    public void handle( PacketResourcePackResponse packet, long currentTimeMillis, PlayerConnection connection ) {
        // TODO: Implement resource pack sending
        System.out.println( packet );

        switch ( packet.getStatus() ) {
            case HAVE_ALL_PACKS:
                PacketResourcePackStack packetResourcePackStack = new PacketResourcePackStack();
                connection.send( packetResourcePackStack );
                break;

            case COMPLETED:
                // Proceed with login
                connection.setState( PlayerConnectionState.LOGIN );
                LOGGER.info( "Logging in as " + connection.getEntity().getName() );

                connection.sendWorldInitialization();
                connection.sendWorldTime( 0 );
                connection.getEntity().updateAttributes();
                connection.sendCommandsEnabled();

                break;
        }
    }
    
}
