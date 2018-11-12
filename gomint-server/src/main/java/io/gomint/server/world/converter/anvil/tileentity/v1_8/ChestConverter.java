/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.tileentity.v1_8;

import io.gomint.server.entity.tileentity.ChestTileEntity;
import io.gomint.server.inventory.item.ItemAir;
import io.gomint.server.inventory.item.ItemStack;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ChestConverter extends BasisConverter<ChestTileEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger( ChestConverter.class );

    public ChestConverter( ApplicationContext context, Object2IntMap<String> itemConverter ) {
        super( context, itemConverter );
    }

    @Override
    public ChestTileEntity readFrom( NBTTagCompound compound ) {
        ChestTileEntity tileEntity = new ChestTileEntity( getBlock( compound ) );
        this.context.getAutowireCapableBeanFactory().autowireBean( tileEntity );

        // Read in items
        List<Object> itemList = compound.getList( "Items", false );
        if ( itemList == null ) {
            // No items ? Return empty chest
            return tileEntity;
        }

        // Iterate over all items
        for ( Object item : itemList ) {
            NBTTagCompound itemCompound = (NBTTagCompound) item;

            ItemStack itemStack = getItemStack( itemCompound );
            if ( itemStack instanceof ItemAir ) {
                continue;
            }

            byte slot = itemCompound.getByte( "Slot", (byte) 127 );
            if ( slot == 127 ) {
                LOGGER.warn( "Found item without slot information: {} @ {} setting it to the next free slot", itemStack.getMaterial(), tileEntity.getBlock().getLocation() );
                for ( int i = 0; i < 27; i++ ) {
                    ItemStack freeItem = (ItemStack) tileEntity.getInventory().getItem( i );
                    if ( freeItem == null ) {
                        tileEntity.getInventory().setItem( i, itemStack );
                        break;
                    }
                }
            } else {
                tileEntity.getInventory().setItem( slot, itemStack );
            }
        }

        return tileEntity;
    }

}
