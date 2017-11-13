package io.gomint.server.world.block;

import io.gomint.inventory.item.*;
import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 12 )
public class Sand extends Block {

    @Override
    public int getBlockId() {
        return 12;
    }

    @Override
    public long getBreakTime() {
        return 750;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return new Class[] {
            ItemIronShovel.class,
            ItemWoodenShovel.class,
            ItemDiamondShovel.class,
            ItemGoldenShovel.class,
            ItemStoneShovel.class
        };
    }

}
