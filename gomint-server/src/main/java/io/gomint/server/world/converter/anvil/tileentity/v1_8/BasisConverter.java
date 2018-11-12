/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.math.Location;
import io.gomint.server.inventory.item.ItemStack;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.world.block.Block;
import io.gomint.server.world.converter.anvil.tileentity.TileEntityConverter;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.BlockType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

/**
 * @param <T> type of tile entity which this converter should generate
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public abstract class BasisConverter<T> implements TileEntityConverter<T> {

    protected final ApplicationContext context;
    protected final Object2IntMap<String> itemConverter;

    /**
     * Read a position from the compound given
     *
     * @param compound which contains x, y and z position integers
     * @return block position object
     */
    protected Block getBlock( NBTTagCompound compound ) {
        Location location = new Location(
            null,
            compound.getInteger( "x", 0 ),
            compound.getInteger( "y", -1 ),
            compound.getInteger( "z", 0 )
        );

        Block block = new Block() {
            @Override
            public float getBlastResistance() {
                return 0;
            }

            @Override
            public BlockType getType() {
                return null;
            }
        };

        block.setLocation( location );
        return block;
    }

    /**
     * Get the item out of the compound
     *
     * @param compound which has serialized information about the item stack
     * @return the item stack which has been stored in the compound
     */
    protected ItemStack getItemStack( NBTTagCompound compound ) {
        Items items = this.context.getBean( Items.class );
        if ( compound == null ) {
            return items.create( 0, (short) 0, (byte) 0, null );
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
            return items.create( 0, (short) 0, (byte) 0, null );
        }

        short data = compound.getShort( "Damage", (short) 0 );
        byte amount = compound.getByte( "Count", (byte) 1 );

        return items.create( material, data, amount, compound.getCompound( "tag", false ) );
    }

}
