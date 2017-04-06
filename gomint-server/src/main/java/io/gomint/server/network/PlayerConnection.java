/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network;

import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.event.player.PlayerLoginEvent;
import io.gomint.jraknet.*;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.async.Delegate;
import io.gomint.server.entity.EntityCow;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.*;
import io.gomint.server.world.CoordinateUtils;
import io.gomint.server.world.WorldAdapter;
import io.gomint.world.Gamemode;
import io.gomint.world.World;
import io.gomint.world.block.Air;
import io.gomint.world.block.Block;
import net.openhft.koloboke.collect.LongCursor;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import static io.gomint.server.network.Protocol.*;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class PlayerConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger( PlayerConnection.class );

    // Network manager that created this connection:
    private final NetworkManager networkManager;

    // Actual connection for wire transfer:
    private final Connection connection;

    // World data
    private final LongSet playerChunks;

    // Commonly used delegates:
    private Delegate<Packet> sendDelegate;

    // Connection State:
    private PlayerConnectionState state;
    private int sentChunks;

    // Entity
    private EntityPlayer entity;
    private LongSet currentlySendingPlayerChunks;

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

        this.playerChunks = HashLongSets.newMutableSet();
        this.currentlySendingPlayerChunks = HashLongSets.newMutableSet();

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
     * Notifies the player connection that the player's view distance was changed somehow. This might
     * result in several packets and chunks to be sent in order to account for the change.
     */
    public void onViewDistanceChanged() {
        this.checkForNewChunks();
        this.sendChunkRadiusUpdate();
    }

    /**
     * Performs a network tick on this player connection. All incoming packets are received and handled
     * accordingly.
     *
     * @param currentMillis Time when the tick started
     */
    public void update( long currentMillis ) {
        // Receive all waiting packets:
        EncapsulatedPacket packetData;
        while ( ( packetData = this.connection.receive() ) != null ) {
            this.handleSocketData( currentMillis, new PacketBuffer( packetData.getPacketData(), 0 ), false );
        }
    }

    /**
     * Sends the given packet to the player.
     *
     * @param packet The packet to send to the player
     */
    public void send( Packet packet ) {
        PacketBuffer buffer = new PacketBuffer( packet.estimateLength() == -1 ? 64 : packet.estimateLength() + 2 );
        buffer.writeByte( (byte) 0xFE );
        buffer.writeByte( packet.getId() );
        packet.serialize( buffer );
        this.connection.send( PacketReliability.RELIABLE, packet.orderingChannel(), buffer.getBuffer(), 0, buffer.getPosition() );
    }

    /**
     * Sends the given packet to the player.
     *
     * @param reliability     The reliability to send the packet with
     * @param orderingChannel The ordering channel to send the packet on
     * @param packet          The packet to send to the player
     */
    public void send( PacketReliability reliability, int orderingChannel, Packet packet ) {
        PacketBuffer buffer = new PacketBuffer( packet.estimateLength() == -1 ? 64 : packet.estimateLength() + 2 );
        buffer.writeByte( (byte) 0xFE );
        buffer.writeByte( packet.getId() );
        packet.serialize( buffer );
        this.connection.send( reliability, orderingChannel, buffer.getBuffer(), 0, buffer.getPosition() );
    }

    /**
     * Sends a world chunk to the player. This is used by world adapters in order to give the player connection
     * a chance to know once it is ready for spawning.
     *
     * @param chunkHash The hash of the chunk to keep track of what the player has loaded
     * @param chunkData The chunk data packet to send to the player
     */
    public void sendWorldChunk( long chunkHash, Packet chunkData ) {
        this.send( chunkData );

        synchronized ( this.playerChunks ) {
            this.currentlySendingPlayerChunks.removeLong( chunkHash );
            this.playerChunks.add( chunkHash );
        }

        if ( this.state == PlayerConnectionState.LOGIN ) {
            this.sentChunks++;

            if ( this.sentChunks == 64 ) {
                int spawnXChunk = CoordinateUtils.fromBlockToChunk( (int) this.entity.getLocation().getX() );
                int spawnZChunk = CoordinateUtils.fromBlockToChunk( (int) this.entity.getLocation().getZ() );

                WorldAdapter worldAdapter = this.entity.getWorld();
                worldAdapter.movePlayerToChunk( spawnXChunk, spawnZChunk, this.entity );

                this.sendPlayState( PacketPlayState.PlayState.SPAWN );
                this.sendWorldTime( 0, false );
                this.sendMovePlayer( this.entity.getLocation() );
                this.state = PlayerConnectionState.PLAYING;
            }
        }
    }

    // ========================================= PACKET HANDLERS ========================================= //

    /**
     * Handles data received directly from the player's connection.
     *
     * @param currentTimeMillis The time in millis of this tick
     * @param buffer            The buffer containing the received data
     * @param batch             Does this packet come out of a batch
     */
    private void handleSocketData( long currentTimeMillis, PacketBuffer buffer, boolean batch ) {
        if ( buffer.getRemaining() <= 0 ) {
            // Malformed packet:
            return;
        }

        // Grab the packet ID from the packet's data
        byte packetId = buffer.readByte();
        if ( packetId == (byte) 0xFE && buffer.getRemaining() > 0 ) {
            packetId = buffer.readByte();
        }

        // If we are still in handshake we only accept certain packets:
        if ( this.state == PlayerConnectionState.HANDSHAKE ) {
            if ( packetId == PACKET_BATCH ) {
                this.handleBatchPacket( currentTimeMillis, buffer, batch );
            } else if ( packetId == PACKET_LOGIN ) {
                PacketLogin login = new PacketLogin();
                login.deserialize( buffer );
                this.handleLoginPacket( login );
            } else {
                LOGGER.error( "Received odd packet" );
            }

            // Don't allow for any other packets if we are in HANDSHAKE state:
            return;
        }

        // When we are in resource pack state
        if ( this.state == PlayerConnectionState.RESOURCE_PACK ) {
            if ( packetId == PACKET_BATCH ) {
                this.handleBatchPacket( currentTimeMillis, buffer, batch );
            } else if ( packetId == PACKET_RESOURCEPACK_RESPONSE ) {
                PacketResourcePackResponse resourcepackResponse = new PacketResourcePackResponse();
                resourcepackResponse.deserialize( buffer );
                this.handleResourceResponse( resourcepackResponse );
            } else {
                LOGGER.error( "Received odd packet" );
            }

            // Don't allow for any other packets if we are in RESOURCE_PACK state:
            return;
        }

        if ( packetId == PACKET_BATCH ) {
            this.handleBatchPacket( currentTimeMillis, buffer, batch );
        } else {
            Packet packet = Protocol.createPacket( packetId );
            if ( packet == null ) {
                this.networkManager.notifyUnknownPacket( packetId, buffer );

                // Got to skip
                buffer.skip( buffer.getRemaining() );
                return;
            }

            packet.deserialize( buffer );
            this.handlePacket( currentTimeMillis, packet );
        }
    }

    /**
     * Handles compressed batch packets directly by decoding their payload.
     *
     * @param buffer The buffer containing the batch packet's data (except packet ID)
     */
    private void handleBatchPacket( long currentTimeMillis, PacketBuffer buffer, boolean batch ) {
        if ( batch ) {
            LOGGER.error( "Malformed batch packet payload: Batch packets are not allowed to contain further batch packets" );
            return;
        }

        int compressedSize = buffer.readUnsignedVarInt();               // Compressed payload length (not of interest; only uncompressed size matters)

        InflaterInputStream inflaterInputStream = new InflaterInputStream( new ByteArrayInputStream( buffer.getBuffer(), buffer.getPosition(), compressedSize ) );

        ByteArrayOutputStream bout = new ByteArrayOutputStream( compressedSize );
        byte[] batchIntermediate = new byte[256];

        try {
            int read;
            while ( ( read = inflaterInputStream.read( batchIntermediate ) ) > -1 ) {
                bout.write( batchIntermediate, 0, read );
            }
        } catch ( IOException e ) {
            LOGGER.error( "Failed to decompress batch packet", e );
            return;
        }

        byte[] payload = bout.toByteArray();

        PacketBuffer payloadBuffer = new PacketBuffer( payload, 0 );
        while ( payloadBuffer.getRemaining() > 0 ) {
            int packetLength = payloadBuffer.readUnsignedVarInt();

            byte[] payData = new byte[packetLength];
            payloadBuffer.readBytes( payData );
            PacketBuffer pktBuf = new PacketBuffer( payData, 0 );
            this.handleSocketData( currentTimeMillis, pktBuf, true );

            if ( pktBuf.getRemaining() > 0 ) {
                LOGGER.error( "Malformed batch packet payload: Could not read enclosed packet data correctly: 0x" + Integer.toHexString( payData[0] ) );
                return;
            }
        }
    }

    /**
     * Handles a deserialized packet by dispatching it to the appropriate handler method.
     *
     * @param currentTimeMillis The time this packet arrived at the network manager
     * @param packet            The packet to handle
     */
    private void handlePacket( long currentTimeMillis, Packet packet ) {
        switch ( packet.getId() ) {
            case PACKET_USE_ITEM:
                this.handleUseItem( (PacketUseItem) packet );
                break;
            case PACKET_MOVE_PLAYER:
                this.handleMovePacket( (PacketMovePlayer) packet );
                break;
            case PACKET_SET_CHUNK_RADIUS:
                this.handleSetChunkRadius( (PacketSetChunkRadius) packet );
                break;
            case PACKET_PLAYER_ACTION:
                this.handlePlayerAction( currentTimeMillis, (PacketPlayerAction) packet );
                break;
            case PACKET_MOB_ARMOR_EQUIPMENT:
                this.handleMobArmorEquipment( (PacketMobArmorEquipment) packet );
                break;
            case PACKET_REMOVE_BLOCK:
                this.handleRemoveBlock( (PacketRemoveBlock) packet );
                break;
            default:
                LOGGER.warn( "No handler for " + packet.getClass() );
                break;
        }
    }

    private void handleRemoveBlock( PacketRemoveBlock packet ) {
        io.gomint.server.world.block.Block block = this.entity.getWorld().getBlockAt( packet.getPosition() );
        if ( block != null ) {
            // Check for special break rights (creative)
            if ( this.entity.getGamemode() == Gamemode.CREATIVE ) {
                block.setType( Air.class );
                return;
            }

            if ( this.entity.getBreakTime() < block.getBreakTime() - 50 ) { // The client can lag one tick behind (yes the client has 20 TPS)
                // Reset block
                PacketUpdateBlock updateBlock = new PacketUpdateBlock();
                updateBlock.setBlockId( block.getBlockId() );
                updateBlock.setPosition( packet.getPosition() );
                updateBlock.setPrioAndMetadata( (byte) ( 0xb << 4 | ( block.getBlockData() & 0xf ) ) );
                send( updateBlock );
            } else {
                // TODO: Add drops

                block.setType( Air.class );
            }
        }
    }

    private void handleUseItem( PacketUseItem packet ) {
        // Only check if distance is under 12 block ( for security )
        if ( this.entity.getLocation().distanceSquared( packet.getPosition() ) < 24 ) {
            // Get block to interact with
            Block block = this.entity.getWorld().getBlockAt( packet.getPosition() );
            ( (io.gomint.server.world.block.Block) block ).interact( this.entity, packet.getFace(), packet.getFacePosition(), packet.getItem() );
        }
    }

    private void handleMobArmorEquipment( PacketMobArmorEquipment packet ) {
        // TODO implement checks if the client says something correct
        this.entity.getInventory().setBoots( packet.getBoots() );
        this.entity.getInventory().setChestplate( packet.getChestplate() );
        this.entity.getInventory().setHelmet( packet.getHelmet() );
        this.entity.getInventory().setLeggings( packet.getLeggings() );
    }

    private void handlePlayerAction( long currentTimeMillis, PacketPlayerAction packet ) {
        System.out.println( packet );

        switch ( packet.getAction() ) {
            case START_BREAK:
                if ( this.entity.getStartBreak() == 0 ) {
                    this.entity.setBreakVector( packet.getPosition() );
                    this.entity.setStartBreak( currentTimeMillis );
                }

                break;

            case ABORT_BREAK:
                this.entity.setBreakVector( null );

            case STOP_BREAK:
                if ( this.entity.getBreakVector() == null ) {
                    // This happens when instant break is enabled
                    this.entity.setBreakTime( 0 );
                    this.entity.setStartBreak( 0 );
                    return;
                }

                this.entity.setBreakTime( ( currentTimeMillis - this.entity.getStartBreak() ) );
                this.entity.setStartBreak( 0 );
                break;
            default:
                LOGGER.warn( "Unhandled action: " + packet );
                break;
        }
    }

    private void handleChat( PacketText packet ) {
        if ( packet.getType() != PacketText.Type.PLAYER_CHAT ) {
            // Players are not allowed to send any other chat messages:
            return;
        }

        // Verify sender:
        if ( !packet.getSender().equals( this.entity.getName() ) ) {
            // Player is trying to fake messages:
            return;
        }

        // TODO: Trigger chat event here
        this.networkManager.broadcast( PacketReliability.RELIABLE, 0, packet );

        Vector position = this.entity.getPosition();
        WorldAdapter world = this.entity.getWorld();
        world.spawnEntityAt( new EntityCow( world ), position.getX(), position.getY(), position.getZ() );
    }

    private void handleSetChunkRadius( PacketSetChunkRadius packet ) {
        // Check if the wanted View distance is under the servers setting
        int oldViewdistance = this.entity.getViewDistance();
        this.entity.setViewDistance( packet.getChunkRadius() );
        if ( oldViewdistance == this.entity.getViewDistance() ) {
            // Got to send confirmation anyways:
            PacketConfirmChunkRadius packetConfirmChunkRadius = new PacketConfirmChunkRadius();
            packetConfirmChunkRadius.setChunkRadius( this.entity.getViewDistance() );
            this.send( packetConfirmChunkRadius );
        }
    }

    private void handleMovePacket( PacketMovePlayer packet ) {
        // TODO: Send some sort of movement event
        if ( (int) this.entity.getLocation().getX() != (int) packet.getX() ||
                (int) this.entity.getLocation().getZ() != (int) packet.getZ() ) {
            this.checkForNewChunks();
        }

        this.entity.setPosition( packet.getX(), packet.getY() - 1.62f, packet.getZ() );
    }

    private void checkForNewChunks() {
        WorldAdapter worldAdapter = this.entity.getWorld();

        int currentXChunk = CoordinateUtils.fromBlockToChunk( (int) this.entity.getLocation().getX() );
        int currentZChunk = CoordinateUtils.fromBlockToChunk( (int) this.entity.getLocation().getZ() );

        int viewDistance = this.entity.getViewDistance();
        synchronized ( this.playerChunks ) {
            for ( int sendXChunk = currentXChunk - viewDistance; sendXChunk < currentXChunk + viewDistance; sendXChunk++ ) {
                for ( int sendZChunk = currentZChunk - viewDistance; sendZChunk < currentZChunk + viewDistance; sendZChunk++ ) {
                    long hash = CoordinateUtils.toLong( sendXChunk, sendZChunk );

                    if ( !this.playerChunks.contains( hash ) &&
                            !this.currentlySendingPlayerChunks.contains( hash ) ) {
                        this.currentlySendingPlayerChunks.add( hash );
                        worldAdapter.sendChunk( sendXChunk, sendZChunk, this.entity );
                    }
                }
            }
        }

        // Move the player to this chunk
        worldAdapter.movePlayerToChunk( currentXChunk, currentZChunk, this.entity );

        // Check for unloading chunks
        synchronized ( this.playerChunks ) {
            LongCursor longCursor = this.playerChunks.cursor();
            while ( longCursor.moveNext() ) {
                int x = (int) ( longCursor.elem() >> 32 );
                int z = (int) ( longCursor.elem() ) + Integer.MIN_VALUE;

                if ( x > currentXChunk + viewDistance ||
                        x < currentXChunk - viewDistance ||
                        z > currentZChunk + viewDistance ||
                        z < currentZChunk - viewDistance ) {
                    // TODO: Check for Packets to send to the client to unload the chunk?
                    longCursor.remove();
                }
            }
        }
    }

    private void handleLoginPacket( PacketLogin packet ) {
        // Check versions
        if ( packet.getProtocol() != RakNetConstraints.MINECRAFT_PE_PROTOCOL_VERSION ) {
            String message;
            if ( packet.getProtocol() < RakNetConstraints.MINECRAFT_PE_PROTOCOL_VERSION ) {
                message = "disconnectionScreen.outdatedClient";
                this.sendPlayState( PacketPlayState.PlayState.LOGIN_FAILED_CLIENT );
            } else {
                message = "disconnectionScreen.outdatedServer";
                this.sendPlayState( PacketPlayState.PlayState.LOGIN_FAILED_SERVER );
            }

            this.disconnect( message );
            return;
        }

        LoginHandler loginHandler = new LoginHandler( packet );
        if ( !loginHandler.isValid() && this.networkManager.getServer().getServerConfig().isOnlyXBOXLogin() ) {
            this.disconnect( "Only valid XBOX Logins are allowed" );
            return;
        }

        // Create entity:
        WorldAdapter world = this.networkManager.getServer().getDefaultWorld();
        this.entity = new EntityPlayer( world, this, loginHandler.getUserName(), loginHandler.getUuid() );

        // Post login event
        PlayerLoginEvent event = this.networkManager.getServer().getPluginManager().callEvent( new PlayerLoginEvent( this.entity ) );
        if ( event.isCancelled() ) {
            this.disconnect( event.getKickMessage() );
            return;
        }

        this.state = PlayerConnectionState.RESOURCE_PACK;
        this.sendPlayState( PacketPlayState.PlayState.LOGIN_SUCCESS );
        this.sendResourcePacks();
    }

    private void handleResourceResponse( PacketResourcePackResponse resourcepackResponse ) {
        // TODO: Implement resource pack sending
        System.out.println( resourcepackResponse );

        switch ( resourcepackResponse.getStatus() ) {
            case HAVE_ALL_PACKS:
                PacketResourcePackStack packetResourcePackStack = new PacketResourcePackStack();
                this.send( packetResourcePackStack );
                break;

            case COMPLETED:
                // Proceed with login
                this.state = PlayerConnectionState.LOGIN;
                LOGGER.info( "Logging in as " + this.entity.getName() );

                this.sendWorldTime( 0, false );
                this.sendWorldInitialization();
                this.sendChunkRadiusUpdate();
                this.sendWorldTime( 0, false );
                this.sendDifficulty();
                this.sendCommandsEnabled();
                this.entity.getAdventureSettings().update();
                this.entity.updateAttributes();

                // Add player to world (will send world chunk packets):
                this.entity.getWorld().addPlayer( this.entity );

                // Now its time for the join event since the play is fully loaded
                this.networkManager.getServer().getPluginManager().callEvent( new PlayerJoinEvent( this.entity ) );

                break;
        }

    }

    private void sendResourcePacks() {
        // We have the chance of forcing resource and behaviour packs here
        PacketResourcePacksInfo packetResourcePacksInfo = new PacketResourcePacksInfo();
        this.send( packetResourcePacksInfo );
    }

    private void sendCommandsEnabled() {
        PacketSetCommandsEnabled packetSetCommandsEnabled = new PacketSetCommandsEnabled();
        packetSetCommandsEnabled.setEnabled( false );   // TODO: Change after command system is there
        this.send( packetSetCommandsEnabled );
    }

    private void sendDifficulty() {
        PacketSetDifficulty packetSetDifficulty = new PacketSetDifficulty();
        packetSetDifficulty.setDifficulty( 1 );
        this.send( packetSetDifficulty );
    }

    private void sendChunkRadiusUpdate() {
        PacketConfirmChunkRadius packetConfirmChunkRadius = new PacketConfirmChunkRadius();
        packetConfirmChunkRadius.setChunkRadius( this.entity.getViewDistance() );
        this.send( packetConfirmChunkRadius );
    }

    public void disconnect( String message ) {
        if ( this.connection.isConnected() && !this.connection.isDisconnecting() ) {
            if ( message != null && message.length() > 0 ) {
                PacketDisconnect packet = new PacketDisconnect();
                packet.setMessage( message );
                this.send( packet );
            }

            if ( this.entity != null ) {
                LOGGER.info( "Player " + this.entity.getName() + " left the game" );
            } else {
                LOGGER.info( "Player has been disconnected whilst logging in" );
            }

            this.connection.disconnect( message );

            // TODO: Player quit event
        }
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
        this.send( packet );
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
        move.setX( location.getX() );
        move.setY( location.getY() );
        move.setZ( location.getZ() );
        move.setYaw( 0.0F );
        move.setPitch( 0.0F );
        move.setMode( (byte) 1 );
        move.setOnGround( false );
        this.send( move );
    }

    /**
     * Sends the player the specified time as world time. The original client sends
     * the current world time every 256 ticks in order to synchronize all client's world
     * times.
     *
     * @param ticks    The current number of ticks of the world time
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
        World world = this.entity.getWorld();

        PacketStartGame packet = new PacketStartGame();
        packet.setEntityId( this.entity.getEntityId() );
        packet.setRuntimeEntityId( 0 );
        packet.setSpawn( world.getSpawnLocation().add( 0, 1.62f, 0 ) );
        packet.setX( (int) world.getSpawnLocation().getX() );
        packet.setY( (int) ( world.getSpawnLocation().getY() + 1.62 ) );
        packet.setZ( (int) world.getSpawnLocation().getZ() );
        packet.setGamemode( 0 );
        packet.setDimension( 0 );
        packet.setSeed( 12345 );
        packet.setGenerator( 1 );
        packet.setDifficulty( 1 );
        packet.setSecret( "1m0AAMIFIgA=" );
        packet.setWorldName( world.getWorldName() );

        this.entity.setPosition( world.getSpawnLocation().add( 0, 1.62f, 0 ) );
        this.send( packet );
    }

    /**
     * The underlying RakNet Connection closed. Cleanup
     */
    public void close() {
        if ( this.entity != null && this.entity.getWorld() != null ) {
            this.entity.getWorld().removePlayer( this.entity );
            this.entity = null;
        }
    }
}
