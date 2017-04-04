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
import io.gomint.server.network.packet.PacketUpdateAttributes;
import io.gomint.server.player.PlayerSkin;
import io.gomint.server.world.WorldAdapter;
import lombok.Getter;
import lombok.Setter;

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
public class EntityPlayer extends EntityLiving implements Player {

    private final PlayerConnection connection;
    private int viewDistance;

    // Player Information
    private String username;
    private UUID uuid;
    private PlayerSkin skin;

    // Inventory
    private PlayerInventory inventory;

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
        System.out.println( viewDistance );
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
    public MetadataContainer getMetadata() {
        MetadataContainer metadata = super.getMetadata();
        metadata.putBoolean( 0, false );
        metadata.putShort( 1, (short) 0x2c01 );
        metadata.putString( 2, this.getName() );
        metadata.putBoolean( 3, true );
        metadata.putBoolean( 4, false );
        metadata.putInt( 7, 0 );
        metadata.putBoolean( 8, false );
        metadata.putBoolean( 15, false );
        metadata.putBoolean( 16, false );
        metadata.putIntTriple( 17, 0, 0, 0 );
        return metadata;
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
