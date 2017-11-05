package io.gomint.inventory.item;

import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface ItemStack {

    /**
     * Get the amount of items in this stack
     *
     * @return amount of items in stack
     */
    byte getAmount();

    /**
     * Get maximum amount which should be possible to store in this stack
     *
     * @return maximum amount of items possible
     */
    byte getMaximumAmount();

    /**
     * Set the amount of items in this stack. This is silently capped to {@link #getMaximumAmount()}
     *
     * @param amount of items which should be in this stack
     */
    void setAmount( int amount );

    /**
     * Get the metadata from this item stack
     *
     * @return metadata from this item stack
     */
    short getData();

    /**
     * Get the NBT data from this item stack
     *
     * @return nbt data from this item stack
     */
    NBTTagCompound getNbtData();

    /**
     * Set a custom name for this item stack
     *
     * @param name of this item stack
     */
    void setCustomName( String name );

    /**
     * Get the custom name of this item
     *
     * @return custom name or null when there is none
     */
    String getCustomName();

    /**
     * Set the lore of this item stack
     *
     * @param lore which should be used in this item stack
     */
    void setLore( String ... lore );

    /**
     * Get the lore of this item stack
     *
     * @return lore of this item stack or null when there is none
     */
    String[] getLore();

}
