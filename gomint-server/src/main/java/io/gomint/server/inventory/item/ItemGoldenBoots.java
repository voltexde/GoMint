package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemType;

import io.gomint.inventory.item.ItemAir;
import io.gomint.math.Vector;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.Block;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 317 )
public class ItemGoldenBoots extends ItemArmor implements io.gomint.inventory.item.ItemGoldenBoots {

    // CHECKSTYLE:OFF
    public ItemGoldenBoots( short data, int amount ) {
        super( 317, data, amount );
    }

    public ItemGoldenBoots( short data, int amount, NBTTagCompound nbt ) {
        super( 317, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public float getReductionValue() {
        return 1;
    }

    @Override
    public boolean interact( EntityPlayer entity, int face, Vector clickPosition, Block clickedBlock ) {
        if ( clickedBlock == null ) {
            if ( isBetter( (ItemStack) entity.getArmorInventory().getBoots() ) ) {
                ItemStack old = (ItemStack) entity.getArmorInventory().getBoots();
                entity.getArmorInventory().setBoots( this );
                entity.getInventory().setItem( entity.getInventory().getItemInHandSlot(), old );
            }
        }

        return false;
    }

    @Override
    public ItemType getType() {
        return ItemType.GOLDEN_BOOTS;
    }

}
