package io.gomint.server.inventory.item;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class ItemArmor extends ItemStack {

    @Override
    public byte getMaximumAmount() {
        return 1;
    }

    public abstract float getReductionValue();

    /**
     * Check if this armor is better than the old one
     *
     * @param oldItem old armor piece
     * @return true if this is better, false if not
     */
    protected boolean isBetter( ItemStack oldItem ) {
        // Armor is better than no armor!
        if ( !( oldItem instanceof ItemArmor ) ) {
            return true;
        }

        return ( (ItemArmor) oldItem ).getReductionValue() < this.getReductionValue();
    }

}
