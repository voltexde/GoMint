package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.registry.RegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 160 )
public class StainedGlassPane extends Block {

    @Override
    public int getBlockId() {
        return 160;
    }

    @Override
    public long getBreakTime() {
        return 450;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public List<ItemStack> getDrops() {
        return new ArrayList<>();
    }


    @Override
    public boolean onBreak() {
        // Send gla

        return super.onBreak();
    }
}
