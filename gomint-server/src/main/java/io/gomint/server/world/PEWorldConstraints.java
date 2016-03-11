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
public final class PEWorldConstraints {

	public static final int MAX_BUILD_HEIGHT = 128;
	public static final int BLOCKS_PER_CHUNK = 16 * 16 * MAX_BUILD_HEIGHT;
	public static final int WATER_LEVEL      = 64;

	private PEWorldConstraints() {
		throw new AssertionError( "Cannot instantiate PEWorldConstraints!" );
	}

}
