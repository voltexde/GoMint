/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world.generator.object;

import io.gomint.GoMint;
import io.gomint.util.random.FastRandom;
import io.gomint.world.World;
import io.gomint.world.block.BlockLeaves;
import io.gomint.world.block.BlockLog;
import io.gomint.world.block.data.WoodType;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BirchTree extends Tree {

    public BirchTree() {
        this.leafBlock = GoMint.instance().createBlock( BlockLeaves.class );
        ( (BlockLeaves) this.leafBlock ).setWoodType( WoodType.BIRCH );

        this.trunkBlock = GoMint.instance().createBlock( BlockLog.class );
        ( (BlockLog) this.trunkBlock ).setWoodType( WoodType.BIRCH );
    }

    @Override
    public void grow( World world, int x, int y, int z, FastRandom random ) {
        this.treeHeight = random.nextInt( 3 ) + 4;
        if ( this.canPlaceObject( world, x, y, z, random ) ) {
            this.placeObject( world, x, y, z, random );
        }
    }

}
