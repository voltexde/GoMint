/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil;

import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PEBlockConverter {

    private Int2ObjectMap<String> converter;

    public PEBlockConverter( List<NBTTagCompound> peConverter ) {
        this.converter = new Int2ObjectOpenHashMap<>();

        for ( NBTTagCompound compound : peConverter ) {
            int oldId = compound.getInteger( "i", 0 );
            String newId = compound.getString( "ni", "minecraft:air" );
            this.converter.put( oldId, newId );
        }
    }

    public String convert( int id ) {
        return this.converter.get( id );
    }

}
