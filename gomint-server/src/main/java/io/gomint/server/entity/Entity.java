/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.server.entity.metadata.MetadataContainer;
import io.gomint.server.world.WorldAdapter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class Entity {

	private static final AtomicLong nextEntityId = new AtomicLong( 0 );

	protected final long          id;
	protected final EntityType   type;
	protected       WorldAdapter world;

	protected Entity( EntityType type, WorldAdapter world ) {
		this.id = nextEntityId.incrementAndGet();
		this.type = type;
		this.world = world;
	}

	/**
	 * Gets this entity's unique ID.
	 *
	 * @return The entitiy's unique ID
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Gets the type of this entity.
	 *
	 * @return The type of this entity
	 */
	public EntityType getType() {
		return this.type;
	}

	/**
	 * Gets the world of this entity.
	 *
	 * @return The world of this entity
	 */
	public WorldAdapter getWorld() {
		return this.world;
	}

	/**
	 * Gets a metadata container containing all metadata values of this entity.
	 *
	 * @return This entity's metadata
	 */
	public MetadataContainer getMetadata() {
		return new MetadataContainer();
	}

}
