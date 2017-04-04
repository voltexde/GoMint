/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.math.Vector;
import io.gomint.world.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlocklightCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger( BlocklightCalculator.class );

    public void calculate( WorldAdapter worldAdapter, Block block ) {
        LOGGER.debug( "Starting to recalc blocklight for " + block.getLocation() );

        Queue<Block> toRelightBlocks = new LinkedList<>();

        toRelightBlocks.add( block );
        while ( toRelightBlocks.size() > 0 ) {
            checkAndRelightSurroundingBlocks( worldAdapter, toRelightBlocks.poll(), toRelightBlocks );
        }
    }

    private void checkAndRelightSurroundingBlocks( WorldAdapter worldAdapter, Block block, Queue<Block> toRelightBlocks ) {
        byte lightLevel = block.getBlockLightLevel();

        // Update in all directions
        setLightLevel( worldAdapter, toRelightBlocks, worldAdapter.getBlockAt( block.getLocation().toVector().add( Vector.UP ) ), lightLevel );
        setLightLevel( worldAdapter, toRelightBlocks, worldAdapter.getBlockAt( block.getLocation().toVector().add( Vector.DOWN ) ), lightLevel );
        setLightLevel( worldAdapter, toRelightBlocks, worldAdapter.getBlockAt( block.getLocation().toVector().add( Vector.EAST ) ), lightLevel );
        setLightLevel( worldAdapter, toRelightBlocks, worldAdapter.getBlockAt( block.getLocation().toVector().add( Vector.NORTH ) ), lightLevel );
        setLightLevel( worldAdapter, toRelightBlocks, worldAdapter.getBlockAt( block.getLocation().toVector().add( Vector.SOUTH ) ), lightLevel );
        setLightLevel( worldAdapter, toRelightBlocks, worldAdapter.getBlockAt( block.getLocation().toVector().add( Vector.WEST ) ), lightLevel );
    }

    private void setLightLevel( WorldAdapter worldAdapter, Queue<Block> toRelightBlocks, Block b1, byte lightLevel ) {
        if ( b1.getSkyLightLevel() > 0 ) {
            // Set light worldAdapter due to skylight not being 0 (so block is exposed to sunlight)
            worldAdapter.setBlockLight( b1.getLocation(), lightLevel );
        }

        if ( ( !b1.isSolid() || b1.isTransparent() ) && b1.getBlockLightLevel() + 2 <= lightLevel ) {
            // Set light worldAdapter due to block being not solid or otherwise transparent
            worldAdapter.setBlockLight( b1.getLocation(), (byte) ( lightLevel - 1 ) );

            // Also add this block to the queue so his neighbours gets updated as well
            toRelightBlocks.add( b1 );
        }
    }

}
