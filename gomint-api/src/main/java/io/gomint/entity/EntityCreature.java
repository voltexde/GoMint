package io.gomint.entity;

import io.gomint.inventory.ArmorInventory;

/**
 * @author geNAZt
 */
public interface EntityCreature extends EntityLiving {

    /**
     * Get the entities armor inventory
     *
     * @return the armor inventory
     */
    ArmorInventory getArmorInventory();

}
