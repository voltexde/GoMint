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
public final class CoordinateUtils {

	/**
	 * Converts a block coordinate to a chunk section coordinate.
	 *
	 * @param v The value to be converted
	 * @return The converted value
	 */
	public static int fromBlockToChunk( int v ) {
		return v >> 4;
	}

	/**
	 * Gets the minimum block coordinate the specified chunk section contains.
	 *
	 * @param v The coordinate of the chunk section
	 * @return The minimum block coordinate contained in the chunk section
	 */
	public static int getChunkMin( int v ) {
		return v << 4;
	}

	/**
	 * Gets the maximum block coordinate the specified chunk section contains.
	 *
	 * @param v The coordinate of the chunk section
	 * @return The maximum block coordinate contained in the chunk section
	 */
	public static int getChunkMax( int v ) {
		return ( ( v + 1 ) << 4 ) - 1;
	}

	/**
	 * Converts a chunk's coordinate to a region file coordinate. Only suitable for x- and z-coordinates.
	 *
	 * @param v The value to be converted
	 * @return The converted value
	 */
	public static int fromChunkToRegion( int v ) {
		return v >> 5;
	}

	/**
	 * Gets the minimum chunk contained in the region.
	 *
	 * @param v The x- or z-coordinate of the region
	 * @return The minimum chunk contained in the region
	 */
	public static int getRegionMin( int v ) {
		return v << 5;
	}

	/**
	 * Gets the maximum chunk contained in the region.
	 *
	 * @param v The x- or z-coordinate of the region
	 * @return The maximum chunk contained in the region
	 */
	public static int getRegionMax( int v ) {
		return ( ( v + 1 ) << 5 ) - 1;
	}

	private CoordinateUtils() {
		throw new AssertionError( "Cannot instantiate CoordinateUtils!" );
	}
	
}
