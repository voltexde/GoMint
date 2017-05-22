package io.gomint.server.network.handler;

import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketConfirmChunkRadius;
import io.gomint.server.network.packet.PacketSetChunkRadius;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketSetChunkRadiusHandler implements PacketHandler<PacketSetChunkRadius> {

    @Override
    public void handle( PacketSetChunkRadius packet, long currentTimeMillis, PlayerConnection connection ) {
        // Check if the wanted View distance is under the servers setting
        int oldViewdistance = connection.getEntity().getViewDistance();
        connection.getEntity().setViewDistance( packet.getChunkRadius() );
        if ( oldViewdistance != connection.getEntity().getViewDistance() ) {
            // Got to send confirmation anyways:
            PacketConfirmChunkRadius packetConfirmChunkRadius = new PacketConfirmChunkRadius();
            packetConfirmChunkRadius.setChunkRadius( connection.getEntity().getViewDistance() );
            connection.send( packetConfirmChunkRadius );
        }
    }

}
