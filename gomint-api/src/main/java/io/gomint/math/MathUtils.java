/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.math;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public final class MathUtils {

	/**
	 * Clamps the specified value into the given range.
	 *
	 * @param v The value to be clamped
	 * @param min The minimum value to clamp to
	 * @param max The maximum value to clamp to
	 * @return The clamped value
	 */
	public static int clamp( int v, int min, int max ) {
		return ( v < min ? min : ( v > max ? max : v ) );
	}

	/**
	 * Clamps the specified value into the given range.
	 *
	 * @param v The value to be clamped
	 * @param min The minimum value to clamp to
	 * @param max The maximum value to clamp to
	 * @return The clamped value
	 */
	public static float clamp( float v, float min, float max ) {
		return ( v < min ? min : ( v > max ? max : v ) );
	}

	/**
	 * Clamps the specified value into the given range.
	 *
	 * @param v The value to be clamped
	 * @param min The minimum value to clamp to
	 * @param max The maximum value to clamp to
	 * @return The clamped value
	 */
	public static double clamp( double v, double min, double max ) {
		return ( v < min ? min : ( v > max ? max : v ) );
	}
	
	private MathUtils() {
		throw new AssertionError( "Cannot instantiate MathUtils!" );
	}
	
}
