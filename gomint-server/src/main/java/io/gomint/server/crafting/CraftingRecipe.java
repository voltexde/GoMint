/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.crafting;

import io.gomint.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Interface which may be expanded in the future but is currently only used for
 * type hinting.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class CraftingRecipe extends Recipe {

	protected ItemStack[] outcome;

	protected CraftingRecipe( ItemStack[] outcome, UUID uuid ) {
		super( uuid );
	}

	@Override
	public Collection<ItemStack> createResult() {
		if ( this.outcome.length == 1 ) {
			return Collections.singletonList( this.outcome[0].clone() );
		} else {
			List<ItemStack> list = new ArrayList<>();
			for ( ItemStack stack : this.outcome ) {
				list.add( stack.clone() );
			}
			return list;
		}
	}

}
