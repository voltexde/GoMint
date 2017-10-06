package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.util.random.WeightedRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 244 )
public class Beetroot extends Growable {

    private static final WeightedRandom<Integer> SEED_RANDOMIZER = new WeightedRandom<>();

    static {
        SEED_RANDOMIZER.add( 0.15, 0 );
        SEED_RANDOMIZER.add( 0.35, 1 );
        SEED_RANDOMIZER.add( 0.35, 2 );
        SEED_RANDOMIZER.add( 0.15, 3 );
    }

    @Override
    public int getBlockId() {
        return 244;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public long getBreakTime() {
        return 0;
    }

    @Override
    public List<ItemStack> getDrops() {
        if ( getBlockData() >= 0x07 ) {
            List<ItemStack> drops = new ArrayList<ItemStack>() {{
                add( Items.create( 457, (short) 0, (byte) 1, null ) ); // Beetroot
            }};

            // Randomize seeds
            int amountOfSeeds = SEED_RANDOMIZER.next();
            if ( amountOfSeeds > 0 ) {
                drops.add( Items.create( 458, (short) 0, (byte) amountOfSeeds, null ) ); // Seeds
            }

            return drops;
        } else {
            return new ArrayList<ItemStack>() {{
                add( Items.create( 458, (short) 0, (byte) 1, null ) ); // Seeds
            }};
        }
    }

}
