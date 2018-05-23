/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.plugin.listener;

import io.gomint.entity.passive.EntityFallingBlock;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.world.BlockBreakEvent;
import io.gomint.math.BlockPosition;
import io.gomint.math.Vector;
import io.gomint.plugin.TestPlugin;
import io.gomint.world.block.Block;
import io.gomint.world.block.BlockAir;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class BlockBreakListener implements EventListener {

    private final TestPlugin plugin;

    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        event.setCancelled( true );

        // Move the block up 5 blocks
        BlockPosition up = event.getBreakBlock().getLocation().clone().add( 0, 5, 0 ).toBlockPosition();
        Block newBlock = event.getBreakBlock().getLocation().getWorld().getBlockAt( up ).copyFromBlock( event.getBreakBlock() );
        event.getBreakBlock().setType( BlockAir.class );

        // Let it drop
        EntityFallingBlock fallingBlock = EntityFallingBlock.create();
        fallingBlock.setBlock( newBlock );

        // Spawn directly on player
        fallingBlock.spawn( event.getPlayer().getLocation() );

        // Ok don't drop
        fallingBlock.setVelocity( new Vector( 0, 0, 0 ) );
        fallingBlock.setAffectedByGravity( false );
        fallingBlock.setImmobile( true );
        fallingBlock.setTicking( false );

        // Render the player invis
        event.getPlayer().setInvisible( true );

        // "Mount" the falling block to the player
        this.plugin.getScheduler().schedule( () -> fallingBlock.teleport( event.getPlayer().getLocation() ), 1, 1, TimeUnit.MILLISECONDS );

        newBlock.setType( BlockAir.class );
    }

}
