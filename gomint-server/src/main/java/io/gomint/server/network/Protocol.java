/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network;

import io.gomint.server.network.packet.*;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public final class Protocol {

    // ========================================= PACKET IDS ========================================= //
    // CHECKSTYLE:OFF
    public static final byte PACKET_LOGIN = (byte) 0x01;
    public static final byte PACKET_PLAY_STATE = (byte) 0x02;
    public static final byte PACKET_DISCONNECT = (byte) 0x05;
    public static final byte PACKET_BATCH = (byte) 0x06;
    public static final byte PACKET_RESOURCEPACK_INFO = (byte) 0x07;
    public static final byte PACKET_RESOURCEPACK_STACK = (byte) 0x08;
    public static final byte PACKET_RESOURCEPACK_RESPONSE = (byte) 0x09;
    public static final byte PACKET_WORLD_TIME = (byte) 0x0b;
    public static final byte PACKET_START_GAME = (byte) 0x0c;
    public static final byte PACKET_SPAWN_PLAYER = (byte) 0x0d;
    public static final byte PACKET_ENTITY_MOVEMENT = (byte) 0x13;
    public static final byte PACKET_MOVE_PLAYER = (byte) 0x14;
    public static final byte PACKET_REMOVE_BLOCK = (byte) 0x16;
    public static final byte PACKET_UPDATE_BLOCK = (byte) 0x17;
    public static final byte PACKET_WORLD_SOUND_EVENT = (byte) 0x1A;
    public static final byte PACKET_MOB_ARMOR_EQUIPMENT = (byte) 0x1C;
    public static final byte PACKET_UPDATE_ATTRIBUTES = (byte) 0x1F;
    public static final byte PACKET_MOB_EQUIPMENT = (byte) 0x20;
    public static final byte PACKET_INTERACT = (byte) 0x22;
    public static final byte PACKET_USE_ITEM = (byte) 0x24;
    public static final byte PACKET_PLAYER_ACTION = (byte) 0x25;
    public static final byte PACKET_MOB_ANIMATION = (byte) 0x28;
    public static final byte PACKET_ANIMATE = (byte) 0x2D;
    public static final byte PACKET_CRAFTING_RECIPES = (byte) 0x2F;
    public static final byte PACKET_ADVENTURE_SETTINGS = (byte) 0x38;
    public static final byte PACKET_WORLD_CHUNK = (byte) 0x3B;
    public static final byte PACKET_SET_COMMANDS_ENABLED = (byte) 0x3C;
    public static final byte PACKET_SET_DIFFICULTY = (byte) 0x3D;
    public static final byte PACKET_SET_GAMEMODE = (byte) 0x3F;
    public static final byte PACKET_PLAYER_LIST = (byte) 0x40;
    public static final byte PACKET_SET_CHUNK_RADIUS = (byte) 0x45;
    public static final byte PACKET_CONFIRM_CHUNK_RADIUS = (byte) 0x46;

    public static final byte PACKET_ENTITY_METADATA = (byte) 0xAD;
    public static final byte PACKET_ENTITY_MOTION = (byte) 0xAE;
    public static final byte PACKET_SET_COMPASS_TARGET = (byte) 0xB1;
    public static final byte PACKET_SPAWN_ENTITY = (byte) 0x98;
    public static final byte PACKET_DESPAWN_ENTITY = (byte) 0x99;
    // CHECKSTYLE:ON

    // ========================================= PACKET METHODS ========================================= //

    private Protocol() {
        throw new AssertionError( "Cannot instantiate Protocol!" );
    }

    /**
     * Creates a new packet instance given the packet ID found inside the first byte of any
     * packet's data.
     *
     * @param id The ID of the the packet to create
     * @return The created packet or null if it could not be created
     */
    public static Packet createPacket( byte id ) {
        switch ( id ) {
            case PACKET_LOGIN:
                return new PacketLogin();

            case PACKET_PLAY_STATE:
                return new PacketPlayState();

            case PACKET_DISCONNECT:
                return new PacketDisconnect();

            case PACKET_BATCH:
                return new PacketBatch();

            case PACKET_RESOURCEPACK_INFO:
                return new PacketResourcePacksInfo();

            case PACKET_WORLD_TIME:
                return new PacketWorldTime();

            case PACKET_RESOURCEPACK_RESPONSE:
                return new PacketResourcePackResponse();

            case PACKET_SPAWN_ENTITY:
                return new PacketSpawnEntity();

            case PACKET_DESPAWN_ENTITY:
                return new PacketDespawnEntity();

            case PACKET_ENTITY_MOVEMENT:
                return new PacketEntityMovement();

            case PACKET_REMOVE_BLOCK:
                return new PacketRemoveBlock();

            case PACKET_WORLD_SOUND_EVENT:
                return new PacketWorldSoundEvent();

            case PACKET_MOVE_PLAYER:
                return new PacketMovePlayer();

            case PACKET_PLAYER_ACTION:
                return new PacketPlayerAction();

            case PACKET_ANIMATE:
                return new PacketAnimate();

            case PACKET_MOB_EQUIPMENT:
                return new PacketMobEquipment();

            case PACKET_INTERACT:
                return new PacketInteract();

            case PACKET_USE_ITEM:
                return new PacketUseItem();

            case PACKET_MOB_ARMOR_EQUIPMENT:
                return new PacketMobArmorEquipment();

            case PACKET_ENTITY_METADATA:
                return new PacketEntityMetadata();

            case PACKET_ENTITY_MOTION:
                return new PacketEntityMotion();

            case PACKET_CRAFTING_RECIPES:
                return new PacketCraftingRecipes();

            case PACKET_WORLD_CHUNK:
                return new PacketWorldChunk();

            case PACKET_SET_COMMANDS_ENABLED:
                return new PacketSetCommandsEnabled();

            case PACKET_SET_DIFFICULTY:
                return new PacketSetDifficulty();

            case PACKET_SET_CHUNK_RADIUS:
                return new PacketSetChunkRadius();

            default:
                return null;
        }
    }

}
