/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
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
	public static final byte PACKET_LOGIN                = (byte) 0x01;
	public static final byte PACKET_PLAY_STATE           = (byte) 0x02;
	public static final byte PACKET_BATCH                = (byte) 0x06;
	public static final byte PACKET_TEXT                 = (byte) 0x07;
	public static final byte PACKET_WORLD_TIME           = (byte) 0x08;
	public static final byte PACKET_WORLD_INITIALIZATION = (byte) 0x09;
	public static final byte PACKET_SPAWN_ENTITY         = (byte) 0x98;
	public static final byte PACKET_DESPAWN_ENTITY       = (byte) 0x99;
	public static final byte PACKET_ENTITY_MOVEMENT      = (byte) 0x0f;
	public static final byte PACKET_MOVE_PLAYER          = (byte) 0x10;
	public static final byte PACKET_MOB_EQUIPMENT        = (byte) 0x1B;
	public static final byte PACKET_MOB_ARMOR_EQUIPMENT  = (byte) 0x1C;
	public static final byte PACKET_PLAYER_ACTION		 = (byte) 0x20;
	public static final byte PACKET_MOB_ANIMATION        = (byte) 0x27;
	public static final byte PACKET_SET_SLOTS 			 = (byte) 0x2C;
	public static final byte PACKET_A8                   = (byte) 0xA8;
	public static final byte PACKET_ENTITY_METADATA      = (byte) 0xAD;
	public static final byte PACKET_ENTITY_MOTION        = (byte) 0xAE;
	public static final byte PACKET_SET_COMPASS_TARGET   = (byte) 0xB1;
	public static final byte PACKET_B9                   = (byte) 0xB9;
	public static final byte PACKET_CRAFTING_RECIPES     = (byte) 0x2F;
	public static final byte PACKET_WORLD_CHUNK          = (byte) 0x34;
	public static final byte PACKET_SET_CHUNK_RADIUS     = (byte) 0x3D;
	public static final byte PACKET_CONFIRM_CHUNK_RADIUS = (byte) 0x3E;
	// CHECKSTYLE:ON

	// ========================================= PACKET METHODS ========================================= //

	/**
	 * Creates a new packet instance given the packet ID found inside the first byte of any
	 * packet's data.
	 *
	 * @param id The ID of the the packet to create
	 *
	 * @return The created packet or null if it could not be created
	 */
	public static Packet createPacket( byte id ) {
		switch ( id ) {
			case PACKET_LOGIN:
				return new PacketLogin();

			case PACKET_PLAY_STATE:
				return new PacketPlayState();

			case PACKET_BATCH:
				return new PacketBatch();

			case PACKET_TEXT:
				return new PacketText();

			case PACKET_WORLD_TIME:
				return new PacketWorldTime();

			case PACKET_WORLD_INITIALIZATION:
				return new PacketWorldInitialization();

			case PACKET_SPAWN_ENTITY:
				return new PacketSpawnEntity();

			case PACKET_DESPAWN_ENTITY:
				return new PacketDespawnEntity();

			case PACKET_ENTITY_MOVEMENT:
				return new PacketEntityMovement();

			case PACKET_MOVE_PLAYER:
				return new PacketMovePlayer();

			case PACKET_PLAYER_ACTION:
				return new PacketPlayerAction();

			case PACKET_SET_SLOTS:
				return new PacketSetSlot();

			case PACKET_MOB_EQUIPMENT:
				return new PacketMobEquipment();

			case PACKET_MOB_ARMOR_EQUIPMENT:
				return new PacketMobArmorEquipment();

			case PACKET_A8:
				return new PacketA8();

			case PACKET_ENTITY_METADATA:
				return new PacketEntityMetadata();

			case PACKET_ENTITY_MOTION:
				return new PacketEntityMotion();

			case PACKET_B9:
				return new PacketB9();

			case PACKET_CRAFTING_RECIPES:
				return new PacketCraftingRecipes();

			case PACKET_WORLD_CHUNK:
				return new PacketWorldChunk();

			case PACKET_SET_CHUNK_RADIUS:
				return new PacketSetChunkRadius();

			default:
				return null;
		}
	}


	private Protocol() {
		throw new AssertionError( "Cannot instantiate Protocol!" );
	}

}
