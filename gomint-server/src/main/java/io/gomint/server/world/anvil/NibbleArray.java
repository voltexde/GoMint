/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class NibbleArray {

	private final int    length;
	private final byte[] data;

	/**
	 * Allocates a new nibble array that is able to hold length entries.
	 *
	 * @param length The desired length of the array
	 */
	public NibbleArray( int length ) {
		this.length = length;
		this.data = new byte[( this.length + 1 ) >> 1];
	}

	/**
	 * Wraps a pre-existing byte array into a nibble array
	 *
	 * @param data The byte array to wrap
	 */
	public NibbleArray( byte[] data ) {
		this.length = data.length << 1;
		this.data = data;
	}

	/**
	 * Sets the entry at the specified index
	 *
	 * @param index The index of the nibble to be set
	 * @param value The value to set
	 */
	public void set( int index, byte value ) {
		if ( ( index & 1 ) == 0 ) {
			this.data[index >> 1] = (byte) ( ( value & 0x0F ) | ( this.data[index >> 1] & 0xF0 ) );
		} else {
			this.data[index >> 1] = (byte) ( ( ( value << 4 ) & 0xF0 ) | ( this.data[index >> 1] & 0x0F ) );
		}
	}

	/**
	 * Gets the entry at the specified index
	 *
	 * @param index The index of the nibble to get
	 *
	 * @return The nibble's value
	 */
	public byte get( int index ) {
		return (byte) ( ( index & 1 ) == 0 ? this.data[index >> 1] & 0x0F : this.data[index >> 1] & 0xF0 );
	}

	/**
	 * Gets the raw byte array held by this nibble array
	 *
	 * @return The raw byte array
	 */
	public byte[] raw() {
		return this.data;
	}

	/**
	 * Gets the length of the nibble array
	 *
	 * @return The length of the nibble array
	 */
	public int length() {
		return this.length;
	}

    /**
     * Return a byte array with all values as a single key. This is double the length as {@link #raw()}.
     *
     * @return Full length data array
     */
    public byte[] toByteArray() {
        byte[] fullData = new byte[this.length];

        for ( int i = 0; i < this.length; i++ ) {
            fullData[i] = get( i );
        }

        return fullData;
    }
}
