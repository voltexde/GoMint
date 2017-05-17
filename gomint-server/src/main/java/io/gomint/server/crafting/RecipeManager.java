/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.crafting;

import io.gomint.server.GoMintServer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketCraftingRecipes;
import io.gomint.server.util.BatchUtil;

import java.util.*;

/**
 * Helper class used to manage all available crafting recipes.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class RecipeManager {

    private final GoMintServer server;
    private Set<Recipe> recipes;
    private Map<UUID, Recipe> lookup;

    private Packet batchPacket;
    private boolean dirty;

    /**
     * Constructs a new recipe manager.
     *
     * @param server The GoMint server instance the recipe manager belongs to
     */
    public RecipeManager( GoMintServer server ) {
        this.server = server;
        this.recipes = new HashSet<>();
        this.lookup = new HashMap<>();
        this.dirty = true;
    }

    /**
     * Gets a packet containing all crafting recipes that may be sent to players in
     * order to let them know what crafting recipes are supported by the server.
     *
     * @return The packet containing all crafting recipes
     */
    public Packet getCraftingRecipesBatch() {
        if ( this.dirty ) {
            PacketCraftingRecipes recipes = new PacketCraftingRecipes();
            recipes.setRecipes( this.recipes );

            this.batchPacket = BatchUtil.batch( recipes );
            this.dirty = false;
        }

        return this.batchPacket;
    }

    /**
     * Registers the given crafting recipe thus making it available for crafting
     * from now on.
     *
     * @param recipe The recipe to register
     */
    public void registerRecipe( Recipe recipe ) {
        this.recipes.add( recipe );

        if ( recipe.getUUID() != null ) {
            this.lookup.put( recipe.getUUID(), recipe );
        }

        this.dirty = true;
    }

    /**
     * Get the stored recipe by its id
     *
     * @param recipeId  The id we should lookup
     * @return either null when no recipe was found or the recipe
     */
    public Recipe getRecipe( UUID recipeId ) {
        return this.lookup.get( recipeId );
    }

}
