package io.gomint.server.network.handler;

import io.gomint.event.player.PlayerLoginEvent;
import io.gomint.event.player.PlayerPreLoginEvent;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.LoginHandler;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.PlayerConnectionState;
import io.gomint.server.network.Protocol;
import io.gomint.server.network.packet.PacketLogin;
import io.gomint.server.network.packet.PacketPlayState;
import io.gomint.server.world.WorldAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketLoginHandler implements PacketHandler<PacketLogin> {

    private static final Logger LOGGER = LoggerFactory.getLogger( PacketLoginHandler.class );

    @Override
    public void handle( PacketLogin packet, long currentTimeMillis, PlayerConnection connection ) {
        PlayerPreLoginEvent playerPreLoginEvent = connection.getNetworkManager().getServer().getPluginManager().callEvent(
                new PlayerPreLoginEvent( connection.getConnection().getAddress() ) 
        );
        
        if ( playerPreLoginEvent.isCancelled() ) {
            // Since the user has not gotten any packets we are not able to be sure if we can send him a disconnect notification
            // so we decide to close the raknet connection without any notice
            connection.disconnect( null );
            return;
        }

        // Check versions
        LOGGER.debug( "Trying to login with protocol version: " + packet.getProtocol() );
        if ( packet.getProtocol() != Protocol.MINECRAFT_PE_PROTOCOL_VERSION ) {
            String message;
            if ( packet.getProtocol() < Protocol.MINECRAFT_PE_PROTOCOL_VERSION ) {
                message = "disconnectionScreen.outdatedClient";
                connection.sendPlayState( PacketPlayState.PlayState.LOGIN_FAILED_CLIENT );
            } else {
                message = "disconnectionScreen.outdatedServer";
                connection.sendPlayState( PacketPlayState.PlayState.LOGIN_FAILED_SERVER );
            }

            connection.disconnect( message );
            return;
        }

        LoginHandler loginHandler = new LoginHandler( packet );
        if ( !loginHandler.isValid() && connection.getNetworkManager().getServer().getServerConfig().isOnlyXBOXLogin() ) {
            connection.disconnect( "Only valid XBOX Logins are allowed" );
            return;
        }

        // Create entity:
        WorldAdapter world = connection.getNetworkManager().getServer().getDefaultWorld();
        connection.setEntity( new EntityPlayer( world, connection, loginHandler.getUserName(), loginHandler.getUuid() ) );
        connection.getEntity().setSkin( loginHandler.getSkin() );
        connection.getEntity().setNameTagVisible( true );
        connection.getEntity().setNameTagAlwaysVisible( true );

        // Post login event
        PlayerLoginEvent event = connection.getNetworkManager().getServer().getPluginManager().callEvent( new PlayerLoginEvent( connection.getEntity() ) );
        if ( event.isCancelled() ) {
            connection.disconnect( event.getKickMessage() );
            return;
        }

        connection.setState( PlayerConnectionState.RESOURCE_PACK );
        connection.sendPlayState( PacketPlayState.PlayState.LOGIN_SUCCESS );
        connection.sendResourcePacks();
    }

}
