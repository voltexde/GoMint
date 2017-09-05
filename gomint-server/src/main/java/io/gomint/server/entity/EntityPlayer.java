/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.entity.Entity;
import io.gomint.entity.Player;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.math.AxisAlignedBB;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.passive.EntityItem;
import io.gomint.server.inventory.InventoryHolder;
import io.gomint.server.inventory.PlayerInventory;
import io.gomint.server.inventory.transaction.TransactionGroup;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.*;
import io.gomint.server.player.PlayerSkin;
import io.gomint.server.util.EnumConnectors;
import io.gomint.server.world.WorldAdapter;
import io.gomint.world.Gamemode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The entity implementation for players. Players are considered living entities even though they
 * do not possess an AI. But as they still move around freely and in an unpredictable fashion
 * (and because we do hope players playing on GoMint actually are living entities) EntityPlayer
 * still inherits from EntityLiving. Their attached behaviour will simply contain no AI states
 * and will not be started either.
 *
 * @author BlackyPaw
 * @version 1.0
 */
@EqualsAndHashCode( callSuper = false, of = { "uuid" } )
public class EntityPlayer extends EntityHuman implements Player, InventoryHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger( EntityPlayer.class );

    private final PlayerConnection connection;
    private int viewDistance;

    // Player Information
    private String username;
    private UUID uuid;
    @Setter
    private PlayerSkin skin;
    private Gamemode gamemode = Gamemode.SURVIVAL;
    @Getter
    private AdventureSettings adventureSettings;
    @Getter
    @Setter
    private Entity hoverEntity;

    // Hidden players
    private LongSet hiddenPlayers;

    // Inventory
    private PlayerInventory inventory;
    @Setter
    @Getter
    private TransactionGroup transactions;
    @Setter
    @Getter
    private EntityItem queuedItemDrop;

    // Block break data
    @Setter
    @Getter
    private Vector breakVector;
    @Setter
    @Getter
    private long startBreak;
    @Setter
    @Getter
    private long breakTime;

    /**
     * Constructs a new player entity which will be spawned inside the specified world.
     *
     * @param world      The world the entity should spawn in
     * @param connection The specific player connection associated with this entity
     * @param username   The name the user has chosen
     * @param uuid       The uuid which has been sent from the client
     */
    public EntityPlayer( WorldAdapter world,
                         PlayerConnection connection,
                         String username,
                         UUID uuid ) {
        super( EntityType.PLAYER, world );
        this.connection = connection;
        this.username = username;
        this.uuid = uuid;
        this.viewDistance = this.world.getServer().getServerConfig().getViewDistance();
        this.adventureSettings = new AdventureSettings( this );
        this.setSize( 0.6f, 1.8f );
        this.eyeHeight = 1.62f;
        this.stepHeight = 0.6f;
        this.initAttributes();

        this.setNameTagAlwaysVisible( true );
        this.setCanClimb( true );
    }

    private void initAttributes() {
        addAttribute( Attribute.HUNGER );
        addAttribute( Attribute.SATURATION );
        addAttribute( Attribute.EXHAUSTION );
        addAttribute( Attribute.EXPERIENCE_LEVEL );
        addAttribute( Attribute.EXPERIENCE );
    }

    // ==================================== ACCESSORS ==================================== //

    /**
     * Gets the view distance set by the player.
     *
     * @return The view distance set by the player
     */
    public int getViewDistance() {
        return this.viewDistance;
    }

    /**
     * Sets the view distance used to calculate the chunk to be sent to the player.
     *
     * @param viewDistance The view distance to set
     */
    public void setViewDistance( int viewDistance ) {
        int tempViewDistance = Math.min( viewDistance, this.getWorld().getServer().getServerConfig().getViewDistance() );
        if ( this.viewDistance != tempViewDistance ) {
            this.viewDistance = tempViewDistance;
            this.connection.onViewDistanceChanged();
        }
    }

    /**
     * Gets the connection associated with this player entity.
     *
     * @return The connection associated with this player entity
     */
    public PlayerConnection getConnection() {
        return this.connection;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public void setGamemode( Gamemode gamemode ) {
        this.gamemode = gamemode;
        this.updateGamemode();
    }

    private void updateGamemode() {
        int gameModeNumber = EnumConnectors.GAMEMODE_CONNECTOR.convert( this.gamemode ).getMagicNumber();

        PacketSetGamemode packetSetGamemode = new PacketSetGamemode();
        packetSetGamemode.setGameMode( gameModeNumber & 0x01 );
        this.connection.send( packetSetGamemode );

        // Recalc adventure settings
        this.adventureSettings.setWorldImmutable( gameModeNumber == 0x03 );
        this.adventureSettings.setCanFly( ( gameModeNumber & 0x01 ) > 0 );
        this.adventureSettings.setNoClip( gameModeNumber == 0x03 );
        this.adventureSettings.setFlying( gameModeNumber == 0x03 );
        this.adventureSettings.setAttackMobs( gameModeNumber < 0x02 );
        this.adventureSettings.setAttackPlayers( gameModeNumber < 0x02 );
        this.adventureSettings.setNoPvP( gameModeNumber == 0x03 );
        this.adventureSettings.update();
    }

    @Override
    public Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public boolean isOp() {
        return this.adventureSettings.isOperator();
    }

    @Override
    public void hidePlayer( Player player ) {
        EntityPlayer other = (EntityPlayer) player;
        if ( other.getWorld().equals( this.getWorld() ) ) {
            if ( this.hiddenPlayers == null ) {
                this.hiddenPlayers = HashLongSets.newMutableSet();
            }

            this.hiddenPlayers.add( other.getEntityId() );

            // Remove player from tablist and from ingame

        }
    }

    @Override
    public void showPlayer( Player player ) {
        if ( this.hiddenPlayers == null ) {
            return;
        }

        if ( this.hiddenPlayers.removeLong( player.getEntityId() ) ) {
            // Send tablist and spawn packet
        }
    }

    @Override
    public boolean isHidden( Player player ) {
        return this.hiddenPlayers != null && this.hiddenPlayers.contains( player.getEntityId() );
    }

    @Override
    public io.gomint.player.PlayerSkin getSkin() {
        return this.skin;
    }

    @Override
    public void teleport( Location to ) {
        // Check if we need to change worlds
        if ( !to.getWorld().equals( getWorld() ) ) {
            // Change worlds first
            this.connection.sendMovePlayer( new Location( to.getWorld(), getPositionX() + 1000000, 4000, getPositionZ() + 1000000 ) );
            getWorld().removePlayer( this );
            despawn();
            setWorld( (WorldAdapter) to.getWorld() );
            this.connection.resetPlayerChunks();
        }

        this.connection.sendMovePlayer( to );

        setPosition( to );
        setPitch( to.getPitch() );
        setYaw( to.getYaw() );
        setHeadYaw( to.getHeadYaw() );

        this.connection.checkForNewChunks();
    }

    // ==================================== UPDATING ==================================== //

    @Override
    public void update( long currentTimeMS, float dT ) {
        super.update( currentTimeMS, dT );

        // Look around
        List<Entity> nearbyEntities = this.world.getNearbyEntities( this.boundingBox.grow( 1, 0.5f, 1 ), this );
        if ( nearbyEntities != null ) {
            for ( Entity nearbyEntity : nearbyEntities ) {
                if ( nearbyEntity instanceof EntityItem ) {
                    EntityItem entityItem = (EntityItem) nearbyEntity;

                    // Check if we can pick it up
                    if ( entityItem.isUnlocked() && currentTimeMS > entityItem.getPickupTime() ) {
                        // Check if we have place in out inventory to store this item
                        if ( !this.inventory.hasPlaceFor( entityItem.getItemStack() ) ) {
                            continue;
                        }

                        // Consume the item
                        PacketPickupItemEntity packet = new PacketPickupItemEntity();
                        packet.setItemEntityId( entityItem.getEntityId() );
                        packet.setPlayerEntityId( this.getEntityId() );

                        for ( Player player : this.world.getPlayers() ) {
                            if ( player instanceof EntityPlayer ) {
                                ( (EntityPlayer) player ).getConnection().addToSendQueue( packet );
                            }
                        }

                        // Manipulate inventory
                        this.inventory.addItem( entityItem.getItemStack() );
                        entityItem.despawn();
                    }
                }
            }
        }

        // Update attributes which are flagged as dirty
        this.updateAttributes();
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * Get the players inventory
     *
     * @return the players inventory
     */
    public PlayerInventory getInventory() {
        return inventory;
    }

    /**
     * Check for attribute updates and send them to the player if needed
     */
    public void updateAttributes() {
        PacketUpdateAttributes updateAttributes = null;

        for ( AttributeInstance instance : attributes.values() ) {
            if ( instance.isDirty() ) {
                if ( updateAttributes == null ) {
                    updateAttributes = new PacketUpdateAttributes();
                    updateAttributes.setEntityId( this.getEntityId() );
                }

                updateAttributes.addAttributeInstance( instance );
            }
        }

        if ( updateAttributes != null ) {
            this.connection.send( updateAttributes );
        }
    }

    /**
     * Fully init inventory and recipes and other stuff which we need to have a full loaded player
     */
    public void fullyInit() {
        this.inventory = new PlayerInventory( this );

        // Testing items
        this.inventory.setItem( 0, new ItemStack( Material.WOOD_PLANKS, 12 ) );
        this.inventory.setItem( 1, new ItemStack( Material.ACACIA_DOOR ) );
        this.inventory.setItem( 2, new ItemStack( Material.DIAMOND_CHESTPLATE ) );

        // Now its time for the join event since the play is fully loaded
        this.getConnection().getNetworkManager().getServer().getPluginManager().callEvent( new PlayerJoinEvent( this ) );

        this.connection.getEntity().getAdventureSettings().update();
        this.connection.getEntity().updateAttributes();
        this.connection.sendPlayState( PacketPlayState.PlayState.SPAWN );

        // Spawn for others
        this.getWorld().spawnEntityAt( this, this.getPositionX(), this.getPositionY(), this.getPositionZ(), this.getYaw(), this.getPitch() );

        PacketPlayerlist playerlist = null;

        // Remap all current living entities
        List<PacketPlayerlist.Entry> listEntry = null;
        for ( Player player : this.getConnection().getEntity().getWorld().getServer().getPlayers() ) {
            if ( !player.isHidden( this ) && !player.equals( this ) ) {
                if ( playerlist == null ) {
                    playerlist = new PacketPlayerlist();
                    playerlist.setMode( (byte) 0 );
                    playerlist.setEntries( new ArrayList<PacketPlayerlist.Entry>() {{
                        add( new PacketPlayerlist.Entry( EntityPlayer.this.getUUID(),
                                EntityPlayer.this.getEntityId(),
                                EntityPlayer.this.getName(),
                                EntityPlayer.this.getSkin() ) );
                    }} );
                }

                ( (EntityPlayer) player ).getConnection().send( playerlist );
            }

            if ( !this.isHidden( player ) && !this.equals( player ) ) {
                if ( listEntry == null ) {
                    listEntry = new ArrayList<>();
                }

                listEntry.add( new PacketPlayerlist.Entry( player.getUUID(), player.getEntityId(), player.getName(), player.getSkin() ) );

                EntityPlayer entityPlayer = (EntityPlayer) player;
                PacketSpawnPlayer spawnPlayer = new PacketSpawnPlayer();
                spawnPlayer.setUuid( entityPlayer.getUUID() );
                spawnPlayer.setName( entityPlayer.getName() );
                spawnPlayer.setEntityId( entityPlayer.getEntityId() );
                spawnPlayer.setRuntimeEntityId( entityPlayer.getEntityId() );

                spawnPlayer.setX( entityPlayer.getPositionX() );
                spawnPlayer.setY( entityPlayer.getPositionY() );
                spawnPlayer.setZ( entityPlayer.getPositionZ() );

                spawnPlayer.setVelocityX( entityPlayer.getMotionX() );
                spawnPlayer.setVelocityY( entityPlayer.getMotionY() );
                spawnPlayer.setVelocityZ( entityPlayer.getMotionZ() );

                spawnPlayer.setPitch( entityPlayer.getPitch() );
                spawnPlayer.setYaw( entityPlayer.getYaw() );
                spawnPlayer.setHeadYaw( entityPlayer.getHeadYaw() );

                spawnPlayer.setItemInHand( entityPlayer.getInventory().getItemInHand() );
                spawnPlayer.setMetadataContainer( entityPlayer.getMetadata() );

                this.getConnection().addToSendQueue( spawnPlayer );
            }
        }

        if ( listEntry != null ) {
            // Send player list
            PacketPlayerlist packetPlayerlist = new PacketPlayerlist();
            packetPlayerlist.setMode( (byte) 0 );
            packetPlayerlist.setEntries( listEntry );
            this.getConnection().send( packetPlayerlist );
        }

        this.getConnection().sendWorldTime( 0 );
        this.sendData( this );
        this.getConnection().sendMovePlayer( this.getLocation() );

        // Send crafting recipes
        PacketBatch batch = new PacketBatch();
        batch.setPayload( this.connection.getEncryptionHandler().encryptInputForClient(
                this.world.getServer().getRecipeManager().getCraftingRecipesBatch().getPayload() ) );
        this.connection.send( batch );
    }

    @Override
    public Packet createSpawnPacket() {
        PacketSpawnPlayer packetSpawnPlayer = new PacketSpawnPlayer();
        packetSpawnPlayer.setUuid( this.getUUID() );
        packetSpawnPlayer.setName( this.getName() );
        packetSpawnPlayer.setEntityId( this.getEntityId() );
        packetSpawnPlayer.setRuntimeEntityId( this.getEntityId() );

        packetSpawnPlayer.setX( this.getPositionX() );
        packetSpawnPlayer.setY( this.getPositionY() );
        packetSpawnPlayer.setZ( this.getPositionZ() );

        packetSpawnPlayer.setVelocityX( this.getMotionX() );
        packetSpawnPlayer.setVelocityY( this.getMotionY() );
        packetSpawnPlayer.setVelocityZ( this.getMotionZ() );

        packetSpawnPlayer.setPitch( this.getPitch() );
        packetSpawnPlayer.setYaw( this.getYaw() );
        packetSpawnPlayer.setHeadYaw( this.getHeadYaw() );

        packetSpawnPlayer.setItemInHand( this.getInventory().getItemInHand() );
        packetSpawnPlayer.setMetadataContainer( this.getMetadata() );
        return packetSpawnPlayer;
    }

}
