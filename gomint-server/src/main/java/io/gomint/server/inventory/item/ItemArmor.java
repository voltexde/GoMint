package io.gomint.server.inventory.item;

import io.gomint.server.registry.SkipRegister;

/**
 * @author geNAZt
 * @version 1.0
 */
@SkipRegister
public abstract class ItemArmor extends ItemStack {

    @Override
    public byte getMaximumAmount() {
        return 1;
    }

    public abstract float getReductionValue();

    protected boolean isBetter( ItemStack oldItem ) {
        // Armor is better than no armor!
        if ( !( oldItem instanceof ItemArmor ) ) {
            return true;
        }

        return ( (ItemArmor) oldItem ).getReductionValue() < this.getReductionValue();
    }

}
