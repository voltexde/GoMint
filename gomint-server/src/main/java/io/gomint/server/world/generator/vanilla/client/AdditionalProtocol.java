/*
 * Copyright (c) 2018 Gomint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.generator.vanilla.client;

import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketAvailableCommands;
import io.gomint.server.network.packet.PacketEntityMetadata;
import io.gomint.server.network.packet.PacketSpawnEntity;
import io.gomint.server.network.packet.PacketSpawnPlayer;
import io.gomint.server.network.packet.PacketStartGame;
import io.gomint.server.network.packet.PacketTileEntityData;
import io.gomint.server.network.packet.PacketWorldChunk;
import io.gomint.server.network.packet.PacketWorldTime;

import static io.gomint.server.network.Protocol.PACKET_AVAILABLE_COMMANDS;
import static io.gomint.server.network.Protocol.PACKET_ENTITY_METADATA;
import static io.gomint.server.network.Protocol.PACKET_SPAWN_ENTITY;
import static io.gomint.server.network.Protocol.PACKET_SPAWN_PLAYER;
import static io.gomint.server.network.Protocol.PACKET_START_GAME;
import static io.gomint.server.network.Protocol.PACKET_TILE_ENTITY_DATA;
import static io.gomint.server.network.Protocol.PACKET_WORLD_CHUNK;
import static io.gomint.server.network.Protocol.PACKET_WORLD_TIME;

/**
 * @author geNAZt
 * @version 1.0
 */
public class AdditionalProtocol {

    /**
     * Creates a new packet instance given the packet ID found inside the first byte of any
     * packet's data.
     *
     * @param id The ID of the the packet to create
     * @return The created packet or null if it could not be created
     */
    public static Packet createPacket( byte id ) {
        switch ( id ) {
            case PACKET_WORLD_CHUNK:
                return new PacketWorldChunk();

            case PACKET_START_GAME:
                return new PacketStartGame();

            default:
                return null;
        }
    }

}
