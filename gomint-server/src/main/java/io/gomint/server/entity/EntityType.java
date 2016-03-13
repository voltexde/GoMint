/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

/**
 * An enumeration of possible entity types.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public enum EntityType {

	/**
	 * This entity is a player.
	 */
	PLAYER( -1 );

	private final int id;

	EntityType( int id ) {
		this.id = id;
	}

	/**
	 * Gets the ID of this entity type.
	 *
	 * @return The ID of this entity type
	 */
	public int getId() {
		return this.id;
	}

}
