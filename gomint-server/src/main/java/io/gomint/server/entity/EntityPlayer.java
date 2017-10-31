/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.entity.ChatType;
import io.gomint.entity.Entity;
import io.gomint.event.entity.EntityDamageByEntityEvent;
import io.gomint.event.entity.EntityDamageEvent;
import io.gomint.event.player.PlayerExhaustEvent;
import io.gomint.event.player.PlayerFoodLevelChangeEvent;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.math.*;
import io.gomint.server.entity.metadata.MetadataContainer;
import io.gomint.server.entity.passive.EntityItem;
import io.gomint.server.inventory.*;
import io.gomint.server.inventory.item.ItemStack;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.*;
import io.gomint.server.permission.PermissionManager;
import io.gomint.server.player.EntityVisibilityManager;
import io.gomint.server.player.PlayerSkin;
import io.gomint.server.util.EnumConnectors;
import io.gomint.server.util.collection.ContainerIDMap;
import io.gomint.server.util.collection.ContainerObjectMap;
import io.gomint.server.util.collection.HiddenPlayerSet;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.block.Block;
import io.gomint.util.Numbers;
import io.gomint.world.Gamemode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

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
@ToString( of = { "username" } )
public class EntityPlayer extends EntityHuman implements io.gomint.entity.EntityPlayer, InventoryHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger( EntityPlayer.class );

    private final PlayerConnection connection;
    private int viewDistance;
    private Queue<ChunkAdapter> chunkSendQueue = new LinkedBlockingQueue<>();

    // EntityPlayer Information
    private String username;
    private UUID uuid;
    private String xboxId;
    @Setter
    private PlayerSkin skin;
    private Gamemode gamemode = Gamemode.SURVIVAL;
    @Getter
    private AdventureSettings adventureSettings;
    @Getter
    @Setter
    private Entity hoverEntity;
    private final PermissionManager permissionManager = new PermissionManager( this );
    @Getter
    private final EntityVisibilityManager entityVisibilityManager = new EntityVisibilityManager( this );
    private Location respawnPosition = null;

    // Hidden players
    private HiddenPlayerSet hiddenPlayers;

    // Inventory
    private PlayerInventory inventory;
    private ArmorInventory armorInventory;
    private Inventory craftingInventory;
    private Inventory cursorInventory;
    private Inventory craftingInputInventory;
    private Inventory craftingResultInventory;
    private ContainerObjectMap windowIds;
    private ContainerIDMap containerIds;

    // Block break data
    @Setter
    @Getter
    private BlockPosition breakVector;
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
     * @param xboxId     The xbox id from xbox live which has logged in
     * @param uuid       The uuid which has been sent from the client
     */
    public EntityPlayer( WorldAdapter world,
                         PlayerConnection connection,
                         String username,
                         String xboxId,
                         UUID uuid ) {
        super( EntityType.PLAYER, world );
        this.connection = connection;
        this.username = username;
        this.xboxId = xboxId;
        this.uuid = uuid;
        this.viewDistance = this.world.getServer().getServerConfig().getViewDistance();
        this.adventureSettings = new AdventureSettings( this );
        this.setSize( 0.6f, 1.8f );
        this.eyeHeight = 1.62f;
        this.stepHeight = 0.6f;
        this.initAttributes();

        this.setNameTagAlwaysVisible( true );
        this.setCanClimb( true );

        this.metadataContainer.putString( MetadataContainer.DATA_NAMETAG, this.username );
        this.metadataContainer.putShort( MetadataContainer.DATA_AIR, (short) 400 );
        this.metadataContainer.putShort( MetadataContainer.DATA_MAX_AIRDATA_MAX_AIR, (short) 400 );
        this.metadataContainer.putFloat( MetadataContainer.DATA_SCALE, 1.0f );
        this.metadataContainer.setDataFlag( MetadataContainer.DATA_INDEX, EntityFlag.BREATHING, true );
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

    @Override
    public void transfer( InetSocketAddress inetSocketAddress ) {
        String address = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        PacketTransfer packetTransfer = new PacketTransfer();
        packetTransfer.setAddress( address );
        packetTransfer.setPort( port );
        this.connection.send( packetTransfer );
    }

    @Override
    public int getPing() {
        return (int) this.connection.getConnection().getPing();
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
    public void hidePlayer( io.gomint.entity.EntityPlayer player ) {
        EntityPlayer other = (EntityPlayer) player;

        if ( this.hiddenPlayers == null ) {
            this.hiddenPlayers = HiddenPlayerSet.withExpectedSize( 5 );
        }

        this.hiddenPlayers.add( other.getEntityId() );

        // Remove the entity clientside
        PacketDespawnEntity packetDespawnEntity = new PacketDespawnEntity();
        packetDespawnEntity.setEntityId( other.getEntityId() );
        getConnection().addToSendQueue( packetDespawnEntity );

        // Remove from player list
        PacketPlayerlist packetPlayerlist = new PacketPlayerlist();
        packetPlayerlist.setMode( (byte) 1 );
        packetPlayerlist.setEntries( new ArrayList<PacketPlayerlist.Entry>() {{
            add( new PacketPlayerlist.Entry( other.getUUID(), other.getEntityId(), null, null, null ) );
        }} );
        getConnection().addToSendQueue( packetPlayerlist );
    }

    @Override
    public void showPlayer( io.gomint.entity.EntityPlayer player ) {
        if ( this.hiddenPlayers == null ) {
            return;
        }

        if ( this.hiddenPlayers.removeLong( player.getEntityId() ) ) {
            EntityPlayer other = (EntityPlayer) player;

            // Send tablist and spawn packet
            PacketPlayerlist packetPlayerlist = new PacketPlayerlist();
            packetPlayerlist.setMode( (byte) 0 );
            packetPlayerlist.setEntries( new ArrayList<PacketPlayerlist.Entry>() {{
                add( new PacketPlayerlist.Entry( other.getUUID(), other.getEntityId(), other.getName(), other.getXboxID(), other.getSkin() ) );
            }} );
            getConnection().addToSendQueue( packetPlayerlist );
            getConnection().addToSendQueue( other.createSpawnPacket() );
        }
    }

    @Override
    public boolean isHidden( io.gomint.entity.EntityPlayer player ) {
        return this.hiddenPlayers != null && this.hiddenPlayers.contains( player.getEntityId() );
    }

    @Override
    public io.gomint.player.PlayerSkin getSkin() {
        return this.skin;
    }

    @Override
    public void teleport( Location to ) {
        Location from = getLocation();

        // Check if we need to change worlds
        if ( !to.getWorld().equals( getWorld() ) ) {
            // Change worlds first
            this.connection.sendMovePlayer( new Location( to.getWorld(), getPositionX() + 1000000, 4000, getPositionZ() + 1000000 ) );
            getWorld().removePlayer( this );
            setWorld( (WorldAdapter) to.getWorld() );
            this.connection.resetPlayerChunks();
            this.entityVisibilityManager.clear();
        }

        this.connection.sendMovePlayer( to );
        this.setAndRecalcPosition( to );
        this.fallDistance = 0;
        this.connection.checkForNewChunks( from );
    }

    @Override
    public void addHunger( float amount ) {
        PlayerFoodLevelChangeEvent foodLevelChangeEvent = new PlayerFoodLevelChangeEvent(
            this, amount
        );
        this.world.getServer().getPluginManager().callEvent( foodLevelChangeEvent );

        if ( !foodLevelChangeEvent.isCancelled() ) {
            super.addHunger( amount );
        } else {
            this.resendAttributes();
        }
    }

    /**
     * Queue which holds chunks to be sent to the client
     *
     * @return queue with chunks to be sent to the client
     */
    public Queue<ChunkAdapter> getChunkSendQueue() {
        return this.chunkSendQueue;
    }

    // ==================================== UPDATING ==================================== //

    @Override
    public void update( long currentTimeMS, float dT ) {
        super.update( currentTimeMS, dT );

        // Update permissions
        this.permissionManager.update( currentTimeMS, dT );

        if ( this.isDead() || this.getHealth() <= 0 ) {
            return;
        }

        // Look around
        Collection<Entity> nearbyEntities = this.world.getNearbyEntities( this.boundingBox.grow( 1, 0.5f, 1 ), this );
        if ( nearbyEntities != null ) {
            for ( Entity nearbyEntity : nearbyEntities ) {
                if ( nearbyEntity instanceof EntityItem ) {
                    EntityItem entityItem = (EntityItem) nearbyEntity;

                    // Check if we can pick it up
                    if ( currentTimeMS > entityItem.getPickupTime() ) {
                        // Check if we have place in out inventory to store this item
                        if ( !this.inventory.hasPlaceFor( entityItem.getItemStack() ) ) {
                            continue;
                        }

                        // Consume the item
                        PacketPickupItemEntity packet = new PacketPickupItemEntity();
                        packet.setItemEntityId( entityItem.getEntityId() );
                        packet.setPlayerEntityId( this.getEntityId() );

                        for ( io.gomint.entity.EntityPlayer player : this.world.getPlayers() ) {
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

    @Override
    public void openInventory( io.gomint.inventory.Inventory inventory ) {
        if ( inventory instanceof ContainerInventory ) {
            // We need to generate a window id for the client
            byte foundId = -1;
            for ( byte i = WindowMagicNumbers.FIRST.getId(); i < WindowMagicNumbers.LAST.getId(); i++ ) {
                if ( !this.windowIds.containsKey( i ) ) {
                    foundId = i;
                    break;
                }
            }

            // No id found?
            if ( foundId == -1 ) {
                return;
            }

            // Trigger open
            ContainerInventory containerInventory = (ContainerInventory) inventory;
            this.windowIds.justPut( foundId, containerInventory );
            this.containerIds.justPut( containerInventory, foundId );
            containerInventory.addViewer( this, foundId );
        }
    }

    /**
     * Get the players armor
     *
     * @return the players armor
     */
    public ArmorInventory getArmorInventory() {
        return armorInventory;
    }

    /**
     * Get the virtual inventory for the crafting slots
     *
     * @return the players crafting field
     */
    public Inventory getCraftingInventory() {
        return craftingInventory;
    }

    /**
     * Get the virtual inventory for the cursor item
     *
     * @return the players cursor item
     */
    public Inventory getCursorInventory() {
        return cursorInventory;
    }

    /**
     * Get the virtual inventory of the current crafting process
     *
     * @return the current crafting input inventory
     */
    public Inventory getCraftingInputInventory() {
        return craftingInputInventory;
    }

    /**
     * Get the virtual inventory of the current crafting process
     *
     * @return the current crafting result inventory
     */
    public Inventory getCraftingResultInventory() {
        return craftingResultInventory;
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
            this.connection.addToSendQueue( updateAttributes );
        }
    }

    /**
     * Force send all attributes
     */
    public void resendAttributes() {
        PacketUpdateAttributes updateAttributes = new PacketUpdateAttributes();
        updateAttributes.setEntityId( this.getEntityId() );

        for ( AttributeInstance instance : attributes.values() ) {
            updateAttributes.addAttributeInstance( instance );
        }

        this.connection.send( updateAttributes );
    }

    /**
     * Fully init inventory and recipes and other stuff which we need to have a full loaded player
     */
    public void fullyInit() {
        this.inventory = new PlayerInventory( this );
        this.armorInventory = new ArmorInventory( this );
        this.craftingInventory = new CraftingInputInventory( this );
        this.cursorInventory = new CursorInventory( this );
        this.craftingInputInventory = new CraftingInputInventory( this );
        this.craftingResultInventory = new CursorInventory( this );
        this.windowIds = ContainerObjectMap.withExpectedSize( 2 );
        this.containerIds = ContainerIDMap.withExpectedSize( 2 );
        this.connection.getServer().getCreativeInventory().addViewer( this );

        // Now its time for the join event since the play is fully loaded
        PlayerJoinEvent event = this.getConnection().getNetworkManager().getServer().getPluginManager().callEvent( new PlayerJoinEvent( this ) );
        if ( event.isCancelled() ) {
            this.connection.disconnect( event.getKickReason() );
            return;
        }

        int gameModeNumber = EnumConnectors.GAMEMODE_CONNECTOR.convert( this.gamemode ).getMagicNumber();
        this.getAdventureSettings().setWorldImmutable( gameModeNumber == 0x03 );
        this.getAdventureSettings().setCanFly( ( gameModeNumber & 0x01 ) > 0 );
        this.getAdventureSettings().setNoClip( gameModeNumber == 0x03 );
        this.getAdventureSettings().setFlying( gameModeNumber == 0x03 );
        this.getAdventureSettings().setAttackMobs( gameModeNumber < 0x02 );
        this.getAdventureSettings().setAttackPlayers( gameModeNumber < 0x02 );
        this.getAdventureSettings().setNoPvP( gameModeNumber == 0x03 );
        this.getAdventureSettings().update();
        this.updateAttributes();
        this.connection.sendPlayState( PacketPlayState.PlayState.SPAWN );

        // Update player list
        PacketPlayerlist playerlist = new PacketPlayerlist();
        playerlist.setMode( (byte) 0 );
        playerlist.setEntries( new ArrayList<PacketPlayerlist.Entry>() {{
            add( new PacketPlayerlist.Entry( uuid, getEntityId(), username, xboxId, skin ) );
        }} );
        this.getConnection().send( playerlist );

        // Spawn for others
        this.getWorld().spawnEntityAt( this, this.getPositionX(), this.getPositionY(), this.getPositionZ(), this.getYaw(), this.getPitch() );

        this.getConnection().sendWorldTime( 0 );
        this.sendData( this );
        this.getConnection().sendMovePlayer( this.getLocation() );

        // Send crafting recipes
        this.connection.send( this.world.getServer().getRecipeManager().getCraftingRecipesBatch() );

        this.sendCommands();
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

    /**
     * Check if the player can interact with the given position
     *
     * @param position    to check for
     * @param maxDistance for which we check
     * @return true if the player can interact, false if not
     */
    public boolean canInteract( Vector position, int maxDistance ) {
        // Distance
        Vector eyePosition = this.getPosition().add( 0, this.getEyeHeight(), 0 );
        if ( eyePosition.distanceSquared( position ) > Numbers.square( maxDistance ) ) {
            return false;
        }

        // Direction
        Vector playerPosition = this.getPosition();
        Vector2 directionPlane = this.getDirectionPlane();
        float dot = directionPlane.dot( new Vector2( eyePosition.getX(), eyePosition.getZ() ) );
        float dot1 = directionPlane.dot( new Vector2( playerPosition.getX(), playerPosition.getZ() ) );
        return ( dot1 - dot ) >= -0.5f;
    }

    /**
     * Remove player from PlayerList and remove from global inventories etc.
     */
    public void cleanup() {
        this.connection.getServer().getCreativeInventory().removeViewer( this );

        Block block = this.world.getBlockAt( this.getPosition().toBlockPosition() );
        block.gotOff( this );

        // Remove from player list
        PacketPlayerlist packetPlayerlist = new PacketPlayerlist();
        packetPlayerlist.setMode( (byte) 1 );
        packetPlayerlist.setEntries( new ArrayList<PacketPlayerlist.Entry>() {{
            add( new PacketPlayerlist.Entry( uuid, getEntityId(), null, null, null ) );
        }} );

        // Cleanup the visibility manager
        this.entityVisibilityManager.clear();

        // Check all other players
        for ( io.gomint.entity.EntityPlayer player : this.connection.getServer().getPlayers() ) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            if ( !entityPlayer.equals( this ) ) {
                entityPlayer.getConnection().addToSendQueue( packetPlayerlist );
            }

            // Check if player did hide this one
            if ( entityPlayer.hiddenPlayers != null && entityPlayer.hiddenPlayers.contains( getEntityId() ) ) {
                entityPlayer.hiddenPlayers.removeLong( getEntityId() );
            }

            // Check if mouseover is the entity
            if ( entityPlayer.hoverEntity != null && entityPlayer.hoverEntity.equals( this ) ) {
                entityPlayer.hoverEntity = null;
            }

            entityPlayer.entityVisibilityManager.removeEntity( this );
        }
    }

    /**
     * Get the window id of specified inventory
     *
     * @param inventory which we want to get the window id for
     * @return window id of the container
     */
    public byte getWindowId( ContainerInventory inventory ) {
        return this.containerIds.getByte( inventory );
    }

    /**
     * Close a container inventory
     *
     * @param windowId which should be closed
     */
    public void closeInventory( byte windowId ) {
        ContainerInventory containerInventory = this.windowIds.remove( windowId );
        if ( containerInventory != null ) {
            containerInventory.removeViewer( this );
            this.containerIds.justRemove( containerInventory );
        }
    }

    /**
     * Get a container by its id
     *
     * @param windowId which should be looked up
     * @return container inventory or null when not found
     */
    public ContainerInventory getContainerId( byte windowId ) {
        return this.windowIds.get( windowId );
    }

    /**
     * Xbox User ID
     *
     * @return xbox user id
     */
    public String getXboxID() {
        return this.xboxId;
    }

    @Override
    public void sendMessage( String message ) {
        PacketText packetText = new PacketText();
        packetText.setMessage( message );
        packetText.setType( PacketText.Type.CLIENT_MESSAGE );
        this.connection.addToSendQueue( packetText );
    }

    @Override
    public void sendMessage( ChatType type, String... message ) {
        PacketText packetText = new PacketText();
        packetText.setMessage( message[0] );
        switch ( type ) {
            case TIP:
                packetText.setType( PacketText.Type.TIP_MESSAGE );
                break;
            case NORMAL:
                packetText.setType( PacketText.Type.CLIENT_MESSAGE );
                break;
            case SYSTEM:
                packetText.setType( PacketText.Type.SYSTEM_MESSAGE );
                break;
            case POPUP:
                packetText.setType( PacketText.Type.POPUP_NOTICE );

                if ( message.length > 1 ) {
                    packetText.setSubtitle( message[1] );
                } else {
                    packetText.setSubtitle( "" );
                }

                break;
            default:
                break;
        }

        this.connection.addToSendQueue( packetText );
    }

    @Override
    public boolean hasPermission( String permission ) {
        return this.permissionManager.hasPermission( permission );
    }

    /**
     * Send commands to the client
     */
    public void sendCommands() {
        // Send commands
        PacketAvailableCommands packetAvailableCommands = this.connection.getServer().
            getPluginManager().getCommandManager().createPacket( this );
        this.connection.send( packetAvailableCommands );
    }

    /**
     * Basicly a override of the {@link EntityHuman#exhaust(float)} method with a event call in it.
     *
     * @param amount of exhaustion
     * @param cause  of exhaustion
     */
    @Override
    public void exhaust( float amount, PlayerExhaustEvent.Cause cause ) {
        if ( this.gamemode == Gamemode.SURVIVAL ) {
            PlayerExhaustEvent exhaustEvent = new PlayerExhaustEvent( this, amount, cause );
            this.world.getServer().getPluginManager().callEvent( exhaustEvent );

            if ( exhaustEvent.isCancelled() ) {
                return;
            }

            super.exhaust( exhaustEvent.getAdditionalAmount() );
        } else {
            if ( this.getExhaustion() != 0 ) {
                this.setExhaustion( 0 );
            }
        }
    }

    /**
     * Handle a jump
     */
    public void jump() {
        // Jumping is only handled for exhaustion it seems
        if ( this.isSprinting() ) {
            this.exhaust( 0.8f, PlayerExhaustEvent.Cause.SPRINT_JUMP );
        } else {
            this.exhaust( 0.2f, PlayerExhaustEvent.Cause.JUMP );
        }
    }

    /**
     * Attack another entity with the item currently in hand
     *
     * @param target which should be attacked
     */
    public void attackWithItemInHand( Entity target ) {
        if ( target instanceof io.gomint.server.entity.Entity ) {
            io.gomint.server.entity.Entity targetEntity = (io.gomint.server.entity.Entity) target;

            // Check if the target can be attacked
            if ( targetEntity.canBeAttackedWithAnItem() ) {
                if ( !targetEntity.isInvulnerableFrom( this ) ) {
                    // Get this entity attack damage
                    EntityDamageEvent.DamageSource damageSource = EntityDamageEvent.DamageSource.ENTITY_ATTACK;
                    float damage = this.getAttribute( Attribute.ATTACK_DAMAGE );

                    // TODO: Enchantment modifiers

                    // Check for knockback stuff
                    int knockbackLevel = 0;

                    if ( this.isSprinting() ) {
                        knockbackLevel++;
                    }

                    // TODO: Add knockback enchantment

                    if ( damage > 0 ) {
                        boolean crit = this.fallDistance > 0 && !this.onGround && !this.isOnLadder() && !this.isInsideLiquid();
                        if ( crit ) {
                            damage *= 1.5;
                        }

                        // Check if target can absorb this damage
                        if ( targetEntity.damage( new EntityDamageByEntityEvent( targetEntity, this, damageSource, damage ) ) ) {
                            // Apply knockback
                            if ( knockbackLevel > 0 ) {
                                // Modify target velocity
                                Vector targetVelo = targetEntity.getVelocity();
                                targetVelo.add(
                                    (float) ( -Math.sin( this.getYaw() * (float) Math.PI / 180.0F ) * (float) knockbackLevel * 0.5F ),
                                    0.1f,
                                    (float) ( Math.cos( this.getYaw() * (float) Math.PI / 180.0F ) * (float) knockbackLevel * 0.5F ) );
                                targetEntity.setVelocity( targetVelo, false );

                                // Modify our velocity / movement
                                Vector ownVelo = this.getVelocity();
                                ownVelo.setX( ownVelo.getX() * 0.6F );
                                ownVelo.setZ( ownVelo.getZ() * 0.6F );
                                this.setVelocity( ownVelo );
                                this.setSprinting( false );
                            }

                            targetEntity.broadCastMotion();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean damage( EntityDamageEvent damageEvent ) {
        // Can't touch this!
        return this.gamemode != Gamemode.CREATIVE && this.gamemode != Gamemode.SPECTATOR && super.damage( damageEvent );
    }

    @Override
    float applyArmorReduction( EntityDamageEvent damageEvent ) {
        if ( damageEvent.getDamageSource() == EntityDamageEvent.DamageSource.FALL ||
            damageEvent.getDamageSource() == EntityDamageEvent.DamageSource.VOID ) {
            return damageEvent.getDamage();
        }

        float damage = damageEvent.getDamage();
        float maxReductionDiff = 25 - this.armorInventory.getTotalArmorValue();
        float amplifiedDamage = damage * maxReductionDiff;
        this.armorInventory.damageEvenly( damage );
        return amplifiedDamage / 25.0F;
    }

    @Override
    public void attach( EntityPlayer player ) {
        super.attach( player );
        this.armorInventory.addViewer( player );

        // Send death animation if needed
        if ( this.getHealth() <= 0 ) {
            PacketEntityEvent entityEvent = new PacketEntityEvent();
            entityEvent.setEntityId( this.getEntityId() );
            entityEvent.setEventId( EntityEvent.DEATH.getId() );
            player.getConnection().addToSendQueue( entityEvent );
        }
    }

    @Override
    public void detach( EntityPlayer player ) {
        this.armorInventory.removeViewer( player );
        super.detach( player );
    }

    public void respawn() {
        // Send metadata
        this.sendData( this );

        // Resend adventure settings
        this.adventureSettings.update();

        // Reset attributes
        this.resetAttributes();
        this.resendAttributes();

        // TODO: Remove effects

        // Check for new chunks
        this.teleport( this.respawnPosition );
        this.respawnPosition = null;

        // Reset motion
        this.setVelocity( new Vector( 0, 0, 0 ) );

        PacketEntityEvent entityEvent = new PacketEntityEvent();
        entityEvent.setEntityId( this.getEntityId() );
        entityEvent.setEventId( EntityEvent.RESPAWN.getId() );

        // Update all other players
        for ( io.gomint.entity.EntityPlayer player : this.world.getPlayers() ) {
            EntityPlayer implPlayer = (EntityPlayer) player;
            implPlayer.getEntityVisibilityManager().updateEntity( this, this.getChunk() );
            if ( implPlayer.getEntityVisibilityManager().isVisible( this ) ) {
                implPlayer.getConnection().send( entityEvent );
            }
        }

        // Apply item in hand stuff
        ItemStack itemInHand = (ItemStack) this.inventory.getItemInHand();
        itemInHand.gotInHand( this );
    }

    @Override
    protected void kill() {
        this.respawnPosition = this.world.getSpawnLocation().add( 0, this.eyeHeight, 0 );

        PacketRespawnPosition respawnPosition = new PacketRespawnPosition();
        respawnPosition.setPosition( this.respawnPosition );
        this.getConnection().addToSendQueue( respawnPosition );

        // Remove entity from all attached players
        for ( io.gomint.entity.EntityPlayer player : this.world.getPlayers() ) {
            EntityPlayer implPlayer = (EntityPlayer) player;
            implPlayer.getEntityVisibilityManager().removeEntity( this, false );
        }
    }

    @Override
    void checkIfCollided( float movX, float movY, float movZ, float dX, float dY, float dZ ) {
        // Check if we are not on ground or we moved on y axis
        if ( !this.onGround || movY != 0 ) {
            AxisAlignedBB bb = this.boundingBox.grow( 0, 0.2f, 0 );

            // Check if we collided with a block
            this.onGround = this.world.getCollisionCubes( this, bb, false ) != null;
        }

        this.isCollided = this.onGround;
    }
}
