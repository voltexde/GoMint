package io.gomint.inventory.item;

import io.gomint.GoMint;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface ItemApple extends ItemFood {

    /**
     * Create a new item stack with given class and amount
     *
     * @param amount which is used for the creation
     */
    static ItemApple create( int amount ) {
        return GoMint.instance().createItemStack( ItemApple.class, amount );
    }

}
