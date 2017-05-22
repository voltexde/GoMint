package io.gomint.server.network.handler;

import io.gomint.inventory.Material;
import io.gomint.server.entity.passive.EntityItem;
import io.gomint.server.inventory.transaction.DropItemTransaction;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketDropItem;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketDropItemHandler implements PacketHandler<PacketDropItem> {

    @Override
    public void handle( PacketDropItem packet, long currentTimeMillis, PlayerConnection connection ) {
        // We can only drop real items
        if ( packet.getItemStack().getMaterial() == Material.AIR ) {
            return;
        }

        // Drop the item now, we may remove it later
        EntityItem drop = new EntityItem( packet.getItemStack(), connection.getEntity().getWorld() );
        drop.setVelocity( connection.getEntity().getDirection().normalize().multiply( 0.4f ) );
        connection.getEntity().getWorld().spawnEntityAt( drop, connection.getEntity().getLocation().add( 0, 1.3f, 0 ) );

        if ( connection.getEntity().getTransactions() == null ) {
            // We will have to queue the item drop until the next inventory update arrives
            connection.getEntity().setQueuedItemDrop( drop );
        } else {
            connection.getEntity().getTransactions().addTransaction( new DropItemTransaction( drop, packet.getItemStack(), currentTimeMillis ) );
            connection.getEntity().getTransactions().tryExecute( currentTimeMillis );
        }
    }

}
