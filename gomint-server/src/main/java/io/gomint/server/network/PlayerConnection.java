/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network;

import io.gomint.jraknet.Connection;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.jraknet.PacketReliability;
import io.gomint.math.Location;
import io.gomint.server.GoMintServer;
import io.gomint.server.async.Delegate;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketA8;
import io.gomint.server.network.packet.PacketB9;
import io.gomint.server.network.packet.PacketBA;
import io.gomint.server.network.packet.PacketEntityMetadata;
import io.gomint.server.network.packet.PacketLogin;
import io.gomint.server.network.packet.PacketPlayState;
import io.gomint.server.network.packet.PacketMovePlayer;
import io.gomint.server.network.packet.PacketWorldInitialization;
import io.gomint.server.network.packet.PacketWorldTime;
import io.gomint.server.player.PlayerSkin;
import io.gomint.server.world.WorldAdapter;

import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static io.gomint.server.network.Protocol.*;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class PlayerConnection {

	// Network manager that created this connection:
	private final NetworkManager networkManager;

	// Actual connection for wire transfer:
	private final Connection connection;

	// Commonly used delegates:
	private Delegate<Packet> sendDelegate;

	// Connection State:
	private PlayerConnectionState state;
	private int sentChunks;

	// Player Information
	private String     username;
	private UUID       uuid;
	private String     secretToken;
	private PlayerSkin skin;

	// Entity
	private EntityPlayer entity;

	// Used for BatchPacket decompression; stored here in order to save allocations at runtime:
	private Inflater batchDecompressor;
	private byte[]   batchIntermediate;

	/**
	 * Constructs a new player connection.
	 *
	 * @param networkManager The network manager creating this instance
	 * @param connection     The jRakNet connection for actual wire-transfer
	 * @param initialState   The player connection's initial state
	 */
	PlayerConnection( NetworkManager networkManager, Connection connection, PlayerConnectionState initialState ) {
		this.networkManager = networkManager;
		this.connection = connection;
		this.state = initialState;

		this.batchDecompressor = new Inflater();
		this.batchIntermediate = new byte[1024];

		this.sendDelegate = new Delegate<Packet>() {
			@Override
			public void invoke( Packet arg ) {
				if ( arg != null ) {
					PlayerConnection.this.send( arg );
				}
			}
		};
	}

	/**
	 * Gets the player connection's current state.
	 *
	 * @return The player connection's current state
	 */
	public PlayerConnectionState getState() {
		return this.state;
	}

	/**
	 * Gets the player's actual jRakNet connection.
	 *
	 * @return The player's actual jRakNet connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * Gets the delegate to be used whenever a packet is to be sent once an asynchronous
	 * operation completes.
	 *
	 * @return The delegate to be used for sending packets asynchronously
	 */
	public Delegate<Packet> getSendDelegate() {
		return this.sendDelegate;
	}

	/**
	 * Gets the EntityPlayer associated with this player connection. This might return null if
	 * the player has not logged in yet.
	 *
	 * @return The entity associated with this player connection
	 */
	public EntityPlayer getEntity() {
		return this.entity;
	}

	/**
	 * Gets the username of this player.
	 *
	 * @return The username of this player
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Performs a network tick on this player connection. All incoming packets are received and handled
	 * accordingly.
	 */
	public void tick() {
		// Receive all waiting packets:
		byte[] packetData;
		while ( ( packetData = this.connection.receive() ) != null ) {
			this.handleSocketData( new PacketBuffer( packetData, 0 ) );
		}
	}

	/**
	 * Sends the given packet to the player.
	 *
	 * @param packet The packet to send to the player
	 */
	public void send( Packet packet ) {
		PacketBuffer buffer = new PacketBuffer( packet.estimateLength() == -1 ? 64 : packet.estimateLength() + 2 );
		buffer.writeByte( (byte) 0x8E );
		buffer.writeByte( packet.getId() );
		packet.serialize( buffer );
		this.connection.send( PacketReliability.RELIABLE, packet.orderingChannel(), buffer.getBuffer(), 0, buffer.getPosition()  );
	}

	/**
	 * Sends the given packet to the player.
	 *
	 * @param reliability The reliability to send the packet with
	 * @param orderingChannel The ordering channel to send the packet on
	 * @param packet The packet to send to the player
	 */
	public void send( PacketReliability reliability, int orderingChannel, Packet packet ) {
		PacketBuffer buffer = new PacketBuffer( packet.estimateLength() == -1 ? 64 : packet.estimateLength() + 2 );
		buffer.writeByte( (byte) 0x8E );
		buffer.writeByte( packet.getId() );
		packet.serialize( buffer );
		this.connection.send( reliability, orderingChannel, buffer.getBuffer(), 0, buffer.getPosition()  );
	}

	/**
	 * Sends a world chunk to the player. This is used by world adapters in order to give the player connection
	 * a chance to know once it is ready for spawning.
	 *
	 * @param chunkData The chunk data packet to send to the player
	 */
	public void sendWorldChunk( Packet chunkData ) {
		this.send( chunkData );
		this.sentChunks++;

		if ( this.sentChunks == 64 ) {
			this.sendWorldTime( 0, false );
			this.sendPlayState( PacketPlayState.PlayState.SPAWN );
		}
	}

	// ========================================= PACKET HANDLERS ========================================= //

	/**
	 * Handles data received directly from the player's connection.
	 *
	 * @param buffer The buffer containing the received data
	 */
	private void handleSocketData( PacketBuffer buffer ) {
		if ( buffer.getRemaining() <= 0 ) {
			// Malformed packet:
			return;
		}

		// Grab the packet ID from the packet's data
		byte packetId = buffer.readByte();
		if ( packetId == (byte) 0x8E && buffer.getRemaining() > 0 ) {
			packetId = buffer.readByte();
		}

		// If we are still in handshake we only accept certain packets:
		if ( this.state == PlayerConnectionState.HANDSHAKE ) {
			if ( packetId == PACKET_BATCH ) {
				this.handleBatchPacket( buffer );
			} else if ( packetId == PACKET_LOGIN ) {
				PacketLogin login = new PacketLogin();
				login.deserialize( buffer );
				this.handleLoginPacket( login );
			}

			// Don't allow for any other packets if we are in HANDSHAKE state:
			return;
		}

		if ( packetId == PACKET_BATCH ) {
			this.handleBatchPacket( buffer );
		} else {
			System.out.println( "Received packet " + packetId );
			Packet packet = Protocol.createPacket( packetId );
			if ( packet == null ) {
				return;
			}

			packet.deserialize( buffer );
			this.handlePacket( packet );
		}
	}

	/**
	 * Handles compressed batch packets directly by decoding their payload.
	 *
	 * @param buffer The buffer containing the batch packet's data (except packet ID)
	 */
	private void handleBatchPacket( PacketBuffer buffer ) {
		buffer.skip( 4 );               // Compressed payload length (not of interest; only uncompressed size matteres)

		this.batchDecompressor.reset();
		this.batchDecompressor.setInput( buffer.getBuffer(), buffer.getPosition(), buffer.getRemaining() );

		byte[] payload;
		try {
			// Only inflate decompressed payload size before allocating the actual payload array:
			this.batchDecompressor.inflate( this.batchIntermediate, 0, 4 );
			int decompressedSize = ( ( this.batchIntermediate[0] & 255 ) << 24 |
			                         ( this.batchIntermediate[1] & 255 ) << 16 |
			                         ( this.batchIntermediate[2] & 255 ) << 8 |
			                         ( this.batchIntermediate[3] & 255 ) );

			if ( decompressedSize < 0 ) {
				this.networkManager.getLogger().warn( "Received malformed batch packet; declared negative payload size (" + decompressedSize + ")" );
				return;
			}

			payload = new byte[decompressedSize];
			this.batchDecompressor.inflate( payload );
		} catch ( DataFormatException e ) {
			this.networkManager.getLogger().warn( "Received malformed batch packet", e );
			return;
		}

		PacketBuffer payloadBuffer = new PacketBuffer( payload, 0 );
		while ( payloadBuffer.getRemaining() > 0 ) {
			this.handleSocketData( payloadBuffer );
		}
	}

	/**
	 * Handles a deserialized packet by dispatching it to the appropriate handler method.
	 *
	 * @param packet The packet to handle
	 */
	private void handlePacket( Packet packet ) {
		switch ( packet.getId() ) {
			case PACKET_MOVE_PLAYER:
				this.send( packet );
				break;
		}
	}

	private void handleLoginPacket( PacketLogin packet ) {
		this.state = PlayerConnectionState.LOGIN;

		this.username = packet.getUsername();
		this.uuid = packet.getClientUUID();
		this.secretToken = packet.getClientSecret();

		this.sendPlayState( PacketPlayState.PlayState.HANDSHAKE );

		// Create entity:
		WorldAdapter world = this.networkManager.getServer().getDefaultWorld();
		this.entity = new EntityPlayer( world, this );

		this.sendWorldInitialization();

		// Add player to world (will send world chunk packets):
		world.addPlayer( this.entity );
	}

	// ====================================== PACKET SENDERS ====================================== //

	/**
	 * Sends a PacketPlayState with the specified state to this player.
	 *
	 * @param state The state to send
	 */
	private void sendPlayState( PacketPlayState.PlayState state ) {
		PacketPlayState packet = new PacketPlayState();
		packet.setState( state );
		this.send( PacketReliability.RELIABLE_ORDERED, 0, packet );
	}

	/**
	 * Sends the player a move player packet which will teleport him to the
	 * given location.
	 *
	 * @param location The location to teleport the player to
	 */
	private void sendMovePlayer( Location location ) {
		PacketMovePlayer move = new PacketMovePlayer();
		move.setEntityId( 0 );                      // All packets referencing the local player have entity ID 0
		move.setX( (float) location.getX() );
		move.setY( (float) location.getY() );
		move.setZ( (float) location.getZ() );
		move.setYaw( 0.0F );
		move.setPitch( 0.0F );
		move.setTeleport( true );
		move.setOnGround( false );
		this.send( move );
	}

	/**
	 * Sends the player the specified time as world time. The original client sends
	 * the current world time every 256 ticks in order to synchronize all client's world
	 * times.
	 *
	 * @param ticks The current number of ticks of the world time
	 * @param counting Whether or not the world time is counting upwards
	 */
	private void sendWorldTime( int ticks, boolean counting ) {
		PacketWorldTime time = new PacketWorldTime();
		time.setTicks( ticks );
		time.setCounting( counting );
		this.send( time );
	}

	/**
	 * Sends a world initialization packet of the world the entity associated with this
	 * connection is currently in to this player.
	 */
	private void sendWorldInitialization() {
		WorldAdapter world = this.entity.getWorld();
		PacketWorldInitialization init = new PacketWorldInitialization();
		init.setSeed( -1 );
		init.setDimension( (byte) 0 );
		init.setGenerator( 0 );
		init.setGamemode( 1 );
		init.setEntityId( 0L );
		init.setSpawnX( (int) world.getSpawnLocation().getX() );
		init.setSpawnY( (int) world.getSpawnLocation().getY() );
		init.setSpawnZ( (int) world.getSpawnLocation().getZ() );
		init.setX( (float) world.getSpawnLocation().getX() );
		init.setY( (float) world.getSpawnLocation().getY() );
		init.setZ( (float) world.getSpawnLocation().getZ() );
		this.send( init );
	}

}
