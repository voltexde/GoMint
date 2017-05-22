package io.gomint.server.network.handler;

import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketMobEquipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketMobEquipmentHandler implements PacketHandler<PacketMobEquipment> {

    private static final Logger LOGGER = LoggerFactory.getLogger( PacketMobEquipmentHandler.class );

    @Override
    public void handle( PacketMobEquipment packet, long currentTimeMillis, PlayerConnection connection ) {
        LOGGER.debug( packet.toString() );
    }

}
