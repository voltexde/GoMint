/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.entity.v1_8;

import io.gomint.math.MathUtils;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.inventory.item.ItemStack;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.world.converter.anvil.entity.EntityConverter;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @param <T> type of entity which this converter should generate
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public abstract class BasisConverter<T extends Entity> implements EntityConverter<T> {

    protected final Items items;
    protected final Object2IntMap<String> itemConverter;

    /**
     * Read a position from the compound given
     *
     * @param compound which contains x, y and z position integers
     * @return block position object
     */
    protected Vector getPosition( NBTTagCompound compound ) {
        // Read position
        List<Object> pos = compound.getList( "Pos", false );
        if ( pos != null ) {
            // Data in the array are three floats (x, y, z)
            float x = MathUtils.ensureFloat( pos.get( 0 ) );
            float y = MathUtils.ensureFloat( pos.get( 1 ) );
            float z = MathUtils.ensureFloat( pos.get( 2 ) );

            return new Vector( x, y, z );
        }

        return null;
    }

    /**
     * Read a position from the compound given
     *
     * @param compound which contains x, y and z position integers
     * @return block position object
     */
    protected Vector getMotion( NBTTagCompound compound ) {
        // Read position
        List<Object> pos = compound.getList( "Motion", false );
        if ( pos != null ) {
            // Data in the array are three floats (x, y, z)
            float x = MathUtils.ensureFloat( pos.get( 0 ) );
            float y = MathUtils.ensureFloat( pos.get( 1 ) );
            float z = MathUtils.ensureFloat( pos.get( 2 ) );

            return new Vector( x, y, z );
        }

        return null;
    }

    public abstract T createEntity();

    /**
     * Get the item out of the compound
     *
     * @param compound which has serialized information about the item stack
     * @return the item stack which has been stored in the compound
     */
    protected ItemStack getItemStack( NBTTagCompound compound ) {
        if ( compound == null ) {
            return this.items.create( 0, (short) 0, (byte) 0, null );
        }

        // Check for correct ids
        int material = 0;
        try {
            material = compound.getShort( "id", (short) 0 );
        } catch ( Exception e ) {
            material = this.itemConverter.getOrDefault( compound.getString( "id", "minecraft:air" ), 0 );
        }

        // Skip non existent items for PE
        if ( material == 0 ) {
            return this.items.create( 0, (short) 0, (byte) 0, null );
        }

        short data = compound.getShort( "Damage", (short) 0 );
        byte amount = compound.getByte( "Count", (byte) 1 );

        return this.items.create( material, data, amount, compound.getCompound( "tag", false ) );
    }

}
