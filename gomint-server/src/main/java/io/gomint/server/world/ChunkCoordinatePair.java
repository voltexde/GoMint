/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class ChunkCoordinatePair implements Cloneable, Comparable<ChunkCoordinatePair> {

	private int x;
	private int z;

	public ChunkCoordinatePair( int x, int z ) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}

	@Override
	public String toString() {
		return "[x=" + this.x + ", z=" + this.z + "]";
	}

	@Override
	public boolean equals( Object other ) {
		if ( this == other ) {
			return true;
		}

		if ( !(other instanceof ChunkCoordinatePair) ) {
			return false;
		}

		ChunkCoordinatePair pair = (ChunkCoordinatePair) other;
		if ( this.x != pair.x || this.z != pair.z ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + this.x;
		hash = 31 * hash + this.z;
		return hash;
	}

	@Override
	public ChunkCoordinatePair clone() {
		try {
			ChunkCoordinatePair pair = (ChunkCoordinatePair) super.clone();
			pair.x = this.x;
			pair.z = this.z;
			return pair;
		} catch ( CloneNotSupportedException ignored ) {
			throw new AssertionError();
		}
	}
	
	@Override
	public int compareTo( ChunkCoordinatePair other ) {
		if ( this.x == other.x && this.z == other.z ) {
			return 0; // Early out
		}

		if ( this.x < other.x ) {
			return -1;
		} else if ( this.x > other.x ) {
			return 1;
		} else {
			if ( this.z < other.z ) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
