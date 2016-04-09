/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.server.entity.metadata.MetadataContainer;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.player.PlayerSkin;
import io.gomint.server.world.WorldAdapter;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class EntityPlayer extends EntityLiving {

	private final PlayerConnection connection;
    private int viewDistance;

    // Player Information
    private String username;
    private UUID uuid;
    private String secretToken;
    private PlayerSkin skin;

	/**
	 * Constructs a new player entity which will be spawned inside the specified world.
	 *
	 * @param world The world the entity should spawn in
	 * @param connection The specific player connection associated with this entity
	 */
	public EntityPlayer( WorldAdapter world,
	                     PlayerConnection connection,
	                     String username,
	                     UUID uuid,
	                     String secretToken ) {
		super( EntityType.PLAYER, world );
		this.connection = connection;
		this.username = username;
		this.uuid = uuid;
		this.secretToken = secretToken;
		this.viewDistance = this.world.getServer().getServerConfig().getViewDistance();
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

	/**
	 * Gets the player's username.
	 *
	 * @return The player's username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Gets the player's UUID which is guaranteed to be unique for all players currently online but
	 * not necessarily for all players in general.
	 *
	 * @return The player's UUID.
	 */
	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Gets the player's secret token. The secret token of a player is a random string sent inside the
	 * login packet the player identified with.
	 *
	 * @return The player's secret token
	 */
	public String getSecretToken() {
		return this.secretToken;
	}

	@Override
	public MetadataContainer getMetadata() {
		MetadataContainer metadata = super.getMetadata();
		metadata.putBoolean( 0, false );
		metadata.putShort( 1, (short) 0x2c01 );
		metadata.putString( 2, this.getUsername() );
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
	}

}
