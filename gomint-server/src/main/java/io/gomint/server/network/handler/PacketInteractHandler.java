package io.gomint.server.network.handler;

import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketInteract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketInteractHandler implements PacketHandler<PacketInteract> {

    private static final Logger LOGGER = LoggerFactory.getLogger( PacketInteract.class );

    @Override
    public void handle( PacketInteract packet, long currentTimeMillis, PlayerConnection connection ) {
        if ( packet.getAction() == PacketInteract.InteractAction.MOUSEOVER ) {
            // Special case runtime entity 0
            if ( packet.getEntityId() == 0 ) {
                connection.getEntity().setHoverEntity( connection.getEntity() );
            } else {
                io.gomint.entity.Entity entity = connection.getEntity().getWorld().findEntity( packet.getEntityId() );
                if ( entity == null ) {
                    LOGGER.warn( connection.getEntity().getName() + ": Wants to hover entity which is not there: " + packet.getEntityId() );
                } else {
                    connection.getEntity().setHoverEntity( entity );
                }
            }
        }
    }

}
