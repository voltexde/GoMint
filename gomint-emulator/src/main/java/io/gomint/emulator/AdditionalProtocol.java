/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.emulator;

import io.gomint.server.network.Protocol;
import io.gomint.server.network.packet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.gomint.server.network.Protocol.PACKET_START_GAME;
import static io.gomint.server.network.Protocol.PACKET_WORLD_TIME;

/**
 * @author geNAZt
 * @version 1.0
 */
public class AdditionalProtocol {

    private static final Logger LOGGER = LoggerFactory.getLogger( Protocol.class );

    /**
     * Creates a new packet instance given the packet ID found inside the first byte of any
     * packet's data.
     *
     * @param id The ID of the the packet to create
     * @return The created packet or null if it could not be created
     */
    public static Packet createPacket( byte id ) {
        switch ( id ) {
            case PACKET_START_GAME:
                return new PacketStartGame();

            case PACKET_WORLD_TIME:
                return new PacketWorldTime();

            default:
                // LOGGER.warn( "Unknown client side packetId: {}", Integer.toHexString( id & 0xFF ) );
                return null;
        }
    }

}
