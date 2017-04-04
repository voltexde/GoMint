/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.game;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public enum Gamemode {

    SURVIVAL( (byte) 0, "Survival" ),
    CREATIVE( (byte) 1, "Creative" ),
    ADVENTURE( (byte) 2, "Adventure" );

    private static final Map<Byte, Gamemode> gamemodesById = new HashMap<>();
    private static final Map<String, Gamemode> gamemodesByName = new HashMap<>();

    static {
        for ( Gamemode gamemode : values() ) {
            gamemodesById.put( gamemode.getId(), gamemode );
            gamemodesByName.put( gamemode.getName().toLowerCase(), gamemode );
        }
    }

    private final byte id;
    private final String name;
    Gamemode( byte id, String name ) {
        this.id = id;
        this.name = name;
    }

    /**
     * Attempts to get a gamemode given its id.
     *
     * @param id The gamemode's id
     * @return The gamemode if found or null otherwise
     */
    public static Gamemode getGamemodeById( byte id ) {
        return gamemodesById.get( id );
    }

    /**
     * Attempts to get a gamemode given its name.
     *
     * @param name The gamemode's name
     * @return The gamemode if found or null otherwise
     */
    public static Gamemode getGamemodeByName( String name ) {
        return gamemodesByName.get( name.toLowerCase() );
    }

    /**
     * Gets the ID of the gammeode.
     *
     * @return The ID of the gamemode
     */
    public byte getId() {
        return this.id;
    }

    /**
     * Gets the name of the gammeode.
     *
     * @return The name of the gamemode
     */
    public String getName() {
        return this.name;
    }

}
