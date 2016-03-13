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
public abstract class AsyncChunkTask {

	public enum Type {

		/**
		 * This async chunk task is about loading a new chunk into memory.
		 */
		LOAD,

		/**
		 * This async chunk task is about saving a chunk back to its file.
		 */
		SAVE,

		/**
		 * This async chunk task is about packaging a chunk into a network ready
		 * batch packet.
		 */
		PACKAGE;

	}

	private final Type type;

	protected AsyncChunkTask( Type type ) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

}
