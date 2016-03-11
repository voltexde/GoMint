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
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketLogin;
import io.gomint.server.network.packet.PacketWorldTime;
import io.gomint.server.player.PlayerSkin;

import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static io.gomint.server.network.Protocol.BATCH_PACKET;
import static io.gomint.server.network.Protocol.LOGIN_PACKET;
import static io.gomint.server.network.Protocol.WORLD_TIME_PACKET;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class PlayerConnection {

	// Network manager that created this connection:
	private final NetworkManager networkManager;

	// Actual connection for wire transfer:
	private final Connection connection;

	// Connection State:
	private PlayerConnectionState state;

	// Player Information
	private String     username;
	private UUID       uuid;
	private String     secretToken;
	private PlayerSkin skin;

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
			if ( packetId == BATCH_PACKET ) {
				this.handleBatchPacket( buffer );
			} else if ( packetId == LOGIN_PACKET ) {
				PacketLogin login = new PacketLogin();
				login.deserialize( buffer );
				this.handleLoginPacket( login );
			}

			// Don't allow for any other packets if we are in HANDSHAKE state:
			return;
		}

		if ( packetId == BATCH_PACKET ) {
			this.handleBatchPacket( buffer );
		} else {
			this.networkManager.notifyUnknownPacket( packetId, buffer );
			/*
			Packet packet = Protocol.createPacket( packetId );
			if ( packet == null ) {
				return;
			}

			packet.deserialize( buffer );
			this.handlePacket( packet );
			*/
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
		System.out.println( "Handling packet" );
		switch ( packet.getId() ) {

		}
	}

	private void handleLoginPacket( PacketLogin packet ) {
		this.state = PlayerConnectionState.LOGIN;

		this.username = packet.getUsername();
		this.uuid = packet.getClientUUID();
		this.secretToken = packet.getClientSecret();

		/*
		byte[] adData = new byte[12];
		adData[0] = (byte) 0xAD;
		adData[1] = (byte) 0xFF;
		adData[2] = (byte) 0xFF;
		adData[3] = (byte) 0xFF;
		adData[4] = (byte) 0xFB;
		adData[5] = (byte) 0x00;
		adData[6] = (byte) 0x00;
		adData[7] = (byte) 0x00;
		adData[8] = (byte) 0x3B;
		adData[9] = (byte) 0x10;
		adData[10] = (byte) 0x01;
		adData[11] = (byte) 0x7F;

		this.connection.send( PacketReliability.RELIABLE, 0, adData );

		PacketWorldTime time = new PacketWorldTime();
		time.setTicks( 0 );
		time.setCounting( true );
		this.send( time );
		*/
	}

}
