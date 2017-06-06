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
                connection.sendChunkRadiusUpdate();
                connection.sendWorldTime( 0, false );
                connection.sendDifficulty();
                connection.sendCommandsEnabled();
                connection.getEntity().getAdventureSettings().update();
                connection.getEntity().updateAttributes();

                // Now its time for the join event since the play is fully loaded
                connection.getNetworkManager().getServer().getPluginManager().callEvent( new PlayerJoinEvent( connection.getEntity() ) );

                // Add player to world (will send world chunk packets):
                connection.getEntity().fullyInit();
                connection.getEntity().getWorld().addPlayer( connection.getEntity() );

                PacketPlayerlist playerlist = null;

                // Remap all current living entities
                List<PacketPlayerlist.Entry> listEntry = null;
                for ( Player player : connection.getEntity().getWorld().getServer().getPlayers() ) {
                    if ( !player.isHidden( connection.getEntity() ) && !player.equals( connection.getEntity() ) ) {
                        if ( playerlist == null ) {
                            playerlist = new PacketPlayerlist();
                            playerlist.setMode( (byte) 0 );
                            playerlist.setEntries( new ArrayList<PacketPlayerlist.Entry>() {{
                                add( new PacketPlayerlist.Entry( connection.getEntity().getUUID(),
                                        connection.getEntity().getEntityId(),
                                        connection.getEntity().getName(),
                                        connection.getEntity().getSkin() ) );
                            }} );
                        }

                        ( (EntityPlayer) player ).getConnection().send( playerlist );
                    }

                    if ( !connection.getEntity().isHidden( player ) && !connection.getEntity().equals( player ) ) {
                        if ( listEntry == null ) {
                            listEntry = new ArrayList<>();
                        }

                        listEntry.add( new PacketPlayerlist.Entry( player.getUUID(), player.getEntityId(), player.getName(), player.getSkin() ) );

                        EntityPlayer entityPlayer = (EntityPlayer) player;
                        PacketSpawnPlayer spawnPlayer = new PacketSpawnPlayer();
                        spawnPlayer.setUuid( entityPlayer.getUUID() );
                        spawnPlayer.setName( entityPlayer.getName() );
                        spawnPlayer.setEntityId( entityPlayer.getEntityId() );
                        spawnPlayer.setRuntimeEntityId( entityPlayer.getEntityId() );

                        spawnPlayer.setX( entityPlayer.getPositionX() );
                        spawnPlayer.setY( entityPlayer.getPositionY() );
                        spawnPlayer.setZ( entityPlayer.getPositionZ() );

                        spawnPlayer.setVelocityX( entityPlayer.getMotionX() );
                        spawnPlayer.setVelocityY( entityPlayer.getMotionY() );
                        spawnPlayer.setVelocityZ( entityPlayer.getMotionZ() );

                        spawnPlayer.setPitch( entityPlayer.getPitch() );
                        spawnPlayer.setYaw( entityPlayer.getYaw() );
                        spawnPlayer.setHeadYaw( entityPlayer.getHeadYaw() );

                        spawnPlayer.setItemInHand( entityPlayer.getInventory().getItemInHand() );
                        spawnPlayer.setMetadataContainer( entityPlayer.getMetadata() );

                        connection.addToSendQueue( spawnPlayer );
                    }
                }

                if ( listEntry != null ) {
                    // Send player list
                    PacketPlayerlist packetPlayerlist = new PacketPlayerlist();
                    packetPlayerlist.setMode( (byte) 0 );
                    packetPlayerlist.setEntries( listEntry );
                    connection.send( packetPlayerlist );
                }

                break;
        }
    }
    
}
