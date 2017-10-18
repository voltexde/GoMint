package io.gomint.server.network.handler;

import io.gomint.entity.Entity;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketAnimate;

import java.util.function.Predicate;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketAnimateHandler implements PacketHandler<PacketAnimate> {

    @Override
    public void handle( PacketAnimate packet, long currentTimeMillis, PlayerConnection connection ) {
        // Simply relay for now
        connection.getEntity().getWorld().sendToVisible( connection.getEntity().getPosition().toBlockPosition(), packet, new Predicate<Entity>() {
            @Override
            public boolean test( Entity entity ) {
                return !connection.getEntity().equals( entity );
            }
        } );
    }

}
