/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.metadata;

import io.gomint.jraknet.PacketBuffer;

import net.openhft.koloboke.collect.map.ByteObjCursor;
import net.openhft.koloboke.collect.map.ByteObjMap;
import net.openhft.koloboke.collect.map.hash.HashByteObjMaps;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class MetadataContainer {

    /**
     * Internal byte representation for a byte meta
     */
	static final byte METADATA_BYTE       = 0;
    /**
     * Internal byte representation for a short meta
     */
	static final byte METADATA_SHORT      = 1;
    /**
     * Internal byte representation for a int meta
     */
	static final byte METADATA_INT        = 2;
    /**
     * Internal byte representation for a string meta
     */
	static final byte METADATA_STRING     = 4;
    /**
     * Internal byte representation for a int triple meta
     */
	static final byte METADATA_INT_TRIPLE = 6;

	private ByteObjMap<MetadataValue> entries;

	/**
	 * Constructs a new, empty metadata container.
	 */
	public MetadataContainer() {
		this( 8 );
	}

	/**
	 * Constructs a new, empty metadata container which may pre-allocate
	 * enough internal capacities to hold at least capacity entries.
	 *
	 * @param capacity The capacity to pre-allocate
	 */
	public MetadataContainer( int capacity ) {
		this.entries = HashByteObjMaps.newMutableMap( ( capacity > 32 ? 32 : capacity ) );
	}

	/**
	 * Puts the specified metadata value into the container.
	 *
	 * @param index The index to put the value into (must be in the range of 0-31)
	 * @param value The value to put into the container
	 */
	public void put( int index, MetadataValue value ) {
		assert ( index >= 0 && index < 32 ) : "Index out of valid range";
		this.entries.put( (byte) index, value );
	}

	/**
	 * Checks whether or not the container holds a value at the specified index.
	 *
	 * @param index The index of the value (must be in the range of 0-31)
	 *
	 * @return Whether or not the container holds a value at the specified index
	 */
	public boolean has( int index ) {
		return this.entries.containsKey( (byte) index );
	}

	/**
	 * Gets the metadata value stored at the specified index.
	 *
	 * @param index The index where the value is stored (must be in the range of 0-31)
	 *
	 * @return The value if found or null otherwise
	 */
	public MetadataValue get( int index ) {
		return this.entries.get( (byte) index );
	}

	/**
	 * Puts a boolean value which will be converted into a byte value internally into the container.
	 *
	 * @param index The index to put the value into
	 * @param value The value to put into the container
	 */
	public void putBoolean( int index, boolean value ) {
		this.putByte( index, (byte) ( value ? 1 : 0 ) );
	}

	/**
	 * Gets a boolean stored inside the specified index.
	 *
	 * @param index The index of the value
	 *
	 * @return The value stored at the specified index
	 *
	 * @throws IllegalArgumentException Thrown in case no value is stored at the specified index or the value is not a boolean
	 */
	public boolean getBoolean( int index ) {
		return ( this.getByte( index ) != 0 );
	}

	/**
	 * Puts a byte value into the container.
	 *
	 * @param index The index to put the value into
	 * @param value The value to put into the container
	 */
	public void putByte( int index, byte value ) {
		this.entries.put( (byte) index, new MetadataByte( value ) );
	}

	/**
	 * Gets a byte stored inside the specified index.
	 *
	 * @param index The index of the value
	 *
	 * @return The value stored at the specified index
	 *
	 * @throws IllegalArgumentException Thrown in case no value is stored at the specified index or the value is not a byte
	 */
	public byte getByte( int index ) {
		MetadataValue value = this.get( index );
		if ( value == null ) {
			throw new IllegalArgumentException( "No value stored at index " + index );
		}

		if ( value.getTypeId() != METADATA_BYTE ) {
			throw new IllegalArgumentException( "Value of different type stored at index " + index );
		}

		return ( (MetadataByte) value ).getValue();
	}

	/**
	 * Puts a short value into the container.
	 *
	 * @param index The index to put the value into
	 * @param value The value to put into the container
	 */
	public void putShort( int index, short value ) {
		this.entries.put( (byte) index, new MetadataShort( value ) );
	}

	/**
	 * Gets a short stored inside the specified index.
	 *
	 * @param index The index of the value
	 *
	 * @return The value stored at the specified index
	 *
	 * @throws IllegalArgumentException Thrown in case no value is stored at the specified index or the value is not a short
	 */
	public short getShort( int index ) {
		MetadataValue value = this.get( index );
		if ( value == null ) {
			throw new IllegalArgumentException( "No value stored at index " + index );
		}

		if ( value.getTypeId() != METADATA_SHORT ) {
			throw new IllegalArgumentException( "Value of different type stored at index " + index );
		}

		return ( (MetadataShort) value ).getValue();
	}

	/**
	 * Puts an int value into the container.
	 *
	 * @param index The index to put the value into
	 * @param value The value to put into the container
	 */
	public void putInt( int index, int value ) {
		this.entries.put( (byte) index, new MetadataInt( value ) );
	}

	/**
	 * Gets an int stored inside the specified index.
	 *
	 * @param index The index of the value
	 *
	 * @return The value stored at the specified index
	 *
	 * @throws IllegalArgumentException Thrown in case no value is stored at the specified index or the value is not an int
	 */
	public int getInt( int index ) {
		MetadataValue value = this.get( index );
		if ( value == null ) {
			throw new IllegalArgumentException( "No value stored at index " + index );
		}

		if ( value.getTypeId() != METADATA_INT ) {
			throw new IllegalArgumentException( "Value of different type stored at index " + index );
		}

		return ( (MetadataInt) value ).getValue();
	}

	/**
	 * Puts a string value into the container.
	 *
	 * @param index The index to put the value into
	 * @param value The value to put into the container
	 */
	public void putString( int index, String value ) {
		this.entries.put( (byte) index, new MetadataString( value ) );
	}

	/**
	 * Gets a string stored inside the specified index.
	 *
	 * @param index The index of the value
	 *
	 * @return The value stored at the specified index
	 *
	 * @throws IllegalArgumentException Thrown in case no value is stored at the specified index or the value is not a string
	 */
	public String getString( int index ) {
		MetadataValue value = this.get( index );
		if ( value == null ) {
			throw new IllegalArgumentException( "No value stored at index " + index );
		}

		if ( value.getTypeId() != METADATA_STRING ) {
			throw new IllegalArgumentException( "Value of different type stored at index " + index );
		}

		return ( (MetadataString) value ).getValue();
	}

	/**
	 * Puts an int triple value into the container.
	 *
	 * @param index The index to put the value into
	 * @param x The x-value of the int triple to put into the container
	 * @param y The y-value of the int triple to put into the container
	 * @param z The z-value of the int triple to put into the container
	 */
	public void putIntTriple( int index, int x, int y, int z ) {
		this.entries.put( (byte) index, new MetadataIntTriple( x, y, z ) );
	}

	/**
	 * Gets an int triple stored inside the specified index.
	 *
	 * @param index The index of the value
	 *
	 * @return The value stored at the specified index
	 *
	 * @throws IllegalArgumentException Thrown in case no value is stored at the specified index or the value is not an int triple
	 */
	public MetadataIntTriple getIntTriple( int index ) {
		MetadataValue value = this.get( index );
		if ( value == null ) {
			throw new IllegalArgumentException( "No value stored at index " + index );
		}

		if ( value.getTypeId() != METADATA_INT_TRIPLE ) {
			throw new IllegalArgumentException( "Value of different type stored at index " + index );
		}

		return ( (MetadataIntTriple) value );
	}

	/**
	 * Serializes this metadata container into the specified buffer.
	 *
	 * @param buffer The buffer to serialize this metadata container into
	 */
	public void serialize( PacketBuffer buffer ) {
		ByteObjCursor<MetadataValue> cursor = this.entries.cursor();

		while ( cursor.moveNext() ) {
			byte index = cursor.key();
			MetadataValue value = cursor.value();
			value.serialize( buffer, index );
		}

		buffer.writeByte( (byte) 0x7F ); // End identifier
	}

	/**
	 * Deserializes this metadata container from the specified buffer.
	 *
	 * @param buffer The buffer to deserialize this metadata container from
	 *
	 * @return Whether or not the metadata container could be deserialized successfully
	 */
	public boolean deserialize( PacketBuffer buffer ) {
		this.entries.clear();

		byte flags = buffer.readByte();
		while ( flags != (byte) 0x7F ) {
			byte type = (byte) ( ( flags & 0xE0 ) >> 5 );
			byte index = (byte) ( flags & 0x1F );

			MetadataValue value = null;
			switch ( type ) {
				case METADATA_BYTE:
					value = new MetadataByte();
					break;
				case METADATA_SHORT:
					value = new MetadataShort();
					break;
				case METADATA_INT:
					value = new MetadataInt();
					break;
				case METADATA_STRING:
					value = new MetadataString();
					break;
				case METADATA_INT_TRIPLE:
					value = new MetadataIntTriple();
					break;
			}

			if ( value == null ) {
				return false;
			}

			value.deserialize( buffer );
			this.entries.put( index, value );
			flags = buffer.readByte();
		}

		return true;
	}

}
