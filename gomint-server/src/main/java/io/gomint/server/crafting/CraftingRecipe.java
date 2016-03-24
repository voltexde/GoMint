/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.crafting;

import java.util.UUID;

/**
 * Interface which may be expanded in the future but is currently only used for
 * type hinting.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class CraftingRecipe extends Recipe {

	protected CraftingRecipe( UUID uuid ) {
		super( uuid );
	}

}
