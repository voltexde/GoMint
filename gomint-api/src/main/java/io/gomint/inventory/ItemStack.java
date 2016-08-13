/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.inventory;

import io.gomint.taglib.NBTTagCompound;

/**
 * Represents a stack of up to 255 items of the same type which may
 * optionally also have an additional data value. May be cloned.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class ItemStack implements Cloneable {

	private short          id;
	private short          data;
	private byte           amount;
	private NBTTagCompound nbt;

	/**
	 * Constructs a new item stack that will hold one item of the
	 * given type.
	 *
	 * @param id The ID of the item
	 */
	public ItemStack( int id ) {
		this( id, (short) 0, 1 );
	}

	/**
	 * Constructs a new item stack that will hold the given amount
	 * of items of the specified type.
	 *
	 * @param id     The ID of the item
	 * @param amount The number of items on this stack (255 max)
	 */
	public ItemStack( int id, int amount ) {
		this( id, (short) 0, amount );
	}

	/**
	 * Constructs a new item stack that will hold the given amount
	 * of items of the specified type. Additionally specifies the
	 * data value of the item.
	 *
	 * @param id     The ID of the item
	 * @param data   The data value of the item
	 * @param amount The number of items on this stack (255 max)
	 */
	public ItemStack( int id, short data, int amount ) {
		this.id = (short) id;
		this.data = data;
		this.amount = (byte) ( amount > Byte.MAX_VALUE ? Byte.MAX_VALUE : amount );
	}

	/**
	 * Constructs a new item stack that will hold the given amount
	 * of items of the specified type. Additionally specifies the
	 * data value of the item as well as raw NBT data that resembles
	 * additional required storage such as a chest's inventory.
	 *
	 * @param id     The ID of the item
	 * @param data   The data value of the item
	 * @param amount The number of items on this stack (255 max)
	 * @param nbt    The additional raw NBT data of the item
	 */
	public ItemStack( int id, short data, int amount, NBTTagCompound nbt ) {
		this( id, data, amount );
		this.nbt = nbt;
	}

	/**
	 * Gets the ID of the item(s) on this stack.
	 *
	 * @return The ID of the item(s) on this stack
	 */
	public short getId() {
		return this.id;
	}

	/**
	 * Sets the ID of the items on this stack.
	 *
	 * @param id The ID of the items on this stack
	 */
	public void setId( int id ) {
		this.id = (short) id;
	}

	/**
	 * The data value of the item(s) on this stack.
	 *
	 * @return The data value of the item(s) on this stack
	 */
	public short getData() {
		return this.data;
	}

	/**
	 * Sets the additional data value of the item(s) on this stack.
	 *
	 * @param data The data value of the item(s) on this stack
	 */
	public void setData( short data ) {
		this.data = data;
	}

	/**
	 * Gets the number of items on this stack.
	 *
	 * @return The number of items on this stack
	 */
	public byte getAmount() {
		return this.amount;
	}

	/**
	 * Sets the number of items on this stack (255 max).
	 *
	 * @param amount The number of items on this stack
	 */
	public void setAmount( int amount ) {
		this.amount = amount > Byte.MAX_VALUE ? Byte.MAX_VALUE : (byte) amount;
	}

	/**
	 * Gets the raw NBT data of the item(s) on this stack.
	 *
	 * @return The raw NBT data of the item(s) on this stack or null
	 */
	public NBTTagCompound getNbtData() {
		return this.nbt;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}

		if ( !( o instanceof ItemStack ) ) {
			return false;
		}

		ItemStack other = (ItemStack) o;
		return ( this.id == other.id && this.data == other.data && this.amount == other.amount );
	}

	@Override
	public int hashCode() {
		int hash = 157;
		hash = 31 * hash + this.id;
		hash = 31 * hash + this.data;
		hash = 31 * hash + this.amount;
		return hash;
	}

	@Override
	public String toString() {
		return String.format( "[ItemStack %d:%d x %d]", this.id, this.data, this.amount );
	}

	@Override
	public ItemStack clone() {
		try {
			ItemStack clone = (ItemStack) super.clone();
			clone.id = this.id;
			clone.data = this.data;
			clone.amount = this.amount;
			clone.nbt = ( this.nbt == null ? null : this.nbt.deepClone() );
			return clone;
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError( "Clone of ItemStack failed", e );
		}
	}

}
