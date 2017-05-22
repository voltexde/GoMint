package io.gomint.server.network.handler;

import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketUseItem;
import io.gomint.world.block.Block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketUseItemHandler implements PacketHandler<PacketUseItem> {

    @Override
    public void handle( PacketUseItem packet, long currentTimeMillis, PlayerConnection connection ) {
        // Only check if distance is under 12 block ( for security )
        if ( connection.getEntity().getLocation().distanceSquared( packet.getPosition() ) < 24 ) {
            // Get block to interact with
            Block block = connection.getEntity().getWorld().getBlockAt( packet.getPosition() );
            ( (io.gomint.server.world.block.Block) block ).interact( connection.getEntity(), packet.getFace(), packet.getFacePosition(), packet.getItem() );
        }
    }

}
