/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.entity.Player;
import io.gomint.math.Vector;
import io.gomint.server.entity.metadata.MetadataContainer;
import io.gomint.server.inventory.PlayerInventory;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketSetGamemode;
import io.gomint.server.network.packet.PacketUpdateAttributes;
import io.gomint.server.player.PlayerSkin;
import io.gomint.server.util.EnumConnector;
import io.gomint.server.world.GamemodeMagicNumbers;
import io.gomint.server.world.WorldAdapter;
import io.gomint.world.Gamemode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;

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
public class EntityPlayer extends EntityLiving implements Player {

    // Enum converters
    private static final EnumConnector<Gamemode, GamemodeMagicNumbers> GAMEMODE_CONNECTOR = new EnumConnector<>( Gamemode.class, GamemodeMagicNumbers.class );

    private final PlayerConnection connection;
    private int viewDistance;

    // Player Information
    private String username;
    private UUID uuid;
    @Setter private PlayerSkin skin;
    private Gamemode gamemode;
    @Getter private AdventureSettings adventureSettings;
    private boolean op;
    @Getter private Vector velocity = new Vector( 0, 0, 0 );

    // Hidden players
    private LongSet hiddenPlayers;

    // Inventory
    private PlayerInventory inventory;

    // Block break data
    @Setter @Getter private Vector breakVector;
    @Setter @Getter private long startBreak;
    @Setter @Getter private long breakTime;

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
        this.inventory = new PlayerInventory( this );
        this.initAttributes();
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
        viewDistance = Math.min( viewDistance, this.getWorld().getServer().getServerConfig().getViewDistance() );
        if ( this.viewDistance != viewDistance ) {
            this.viewDistance = viewDistance;
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
        int gameModeNumber = GAMEMODE_CONNECTOR.convert( this.gamemode ).getMagicNumber();

        PacketSetGamemode packetSetGamemode = new PacketSetGamemode();
        packetSetGamemode.setGameMode( gameModeNumber & 0x01 );
        this.connection.send( packetSetGamemode );

        // Recalc adventure settings
        this.adventureSettings.setCanDestroyBlock( ( gameModeNumber & 0x02 ) == 0 );
        this.adventureSettings.setCanFly( ( gameModeNumber & 0x01 ) > 0 );
        this.adventureSettings.setNoclip( gameModeNumber == 0x03 );
        this.adventureSettings.setFlying( gameModeNumber == 0x03 );
        this.adventureSettings.setNoMvP( gameModeNumber == 0x03 );
        this.adventureSettings.setNoPvM( gameModeNumber == 0x03 );
        this.adventureSettings.setNoPvP( gameModeNumber == 0x03 );
        this.adventureSettings.update();
    }

    @Override
    public Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public boolean isOp() {
        return this.op;
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

    // ==================================== UPDATING ==================================== //

    @Override
    public void update( long currentTimeMS, float dT ) {
        super.update( currentTimeMS, dT );

        // Update attributes which are flagged as dirty
        this.updateAttributes();
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public void updateAttributes() {
        PacketUpdateAttributes updateAttributes = null;

        for ( AttributeInstance instance : attributes.values() ) {
            if ( instance.isDirty() ) {
                if ( updateAttributes == null ) {
                    updateAttributes = new PacketUpdateAttributes();
                    updateAttributes.setEntityId( 0 );
                }

                updateAttributes.addAttributeInstance( instance );
            }
        }

        if ( updateAttributes != null ) {
            this.connection.send( updateAttributes );
        }
    }

}
