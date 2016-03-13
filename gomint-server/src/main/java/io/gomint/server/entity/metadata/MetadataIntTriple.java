/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.metadata;

import io.gomint.jraknet.PacketBuffer;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class MetadataIntTriple extends MetadataValue {

	private int x;
	private int y;
	private int z;

	/**
	 * Constructs a new metadata int triple
	 */
	public MetadataIntTriple() {

	}

	/**
	 * Constructs a new metadata int triple and initializes its values.
	 *
	 * @param x The x-value to set
	 * @param y The y-value to set
	 * @param z The z-value to set
	 */
	public MetadataIntTriple( int x, int y, int z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Gets the x-value of this metadata int triple.
	 *
	 * @return The x-value of this metadata int triple
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Gets the y-value of this metadata int triple.
	 *
	 * @return The y-value of this metadata int triple
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Gets the z-value of this metadata int triple.
	 *
	 * @return The z-value of this metadata int triple
	 */
	public int getZ() {
		return this.z;
	}

	/**
	 * Sets the x-value of this metadata int triple.
	 *
	 * @param x The x-value to set
	 */
	public void setX( int x ) {
		this.x = x;
	}

	/**
	 * Sets the y-value of this metadata int triple.
	 *
	 * @param y The y-value to set
	 */
	public void setY( int y ) {
		this.y = y;
	}

	/**
	 * Sets the z-value of this metadata int triple.
	 *
	 * @param z The z-value to set
	 */
	public void setZ( int z ) {
		this.z = z;
	}

	/**
	 * Sets all values of this metadata int triple.
	 *
	 * @param x The x-value to set
	 * @param y The y-value to set
	 * @param z The z-value to set
	 */
	public void set( int x, int y, int z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// ========================== METADATA VALUE ========================== //
	@Override
	void serialize( PacketBuffer buffer, int index ) {
		super.serialize( buffer, index );
		buffer.writeInt( this.x );
		buffer.writeInt( this.y );
		buffer.writeInt( this.z );
	}

	@Override
	void deserialize( PacketBuffer buffer ) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	@Override
	byte getTypeId() {
		return MetadataContainer.METADATA_INT_TRIPLE;
	}
}
