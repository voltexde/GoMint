package io.gomint.plugin.listener;

import io.gomint.entity.ChatType;
import io.gomint.entity.passive.EntityFallingBlock;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerMoveEvent;
import io.gomint.math.BlockPosition;
import io.gomint.plugin.TestPlugin;
import io.gomint.world.block.Block;
import io.gomint.world.block.BlockAir;
import io.gomint.world.block.BlockStone;
import io.gomint.world.block.BlockWood;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlayerMoveListener implements EventListener {

    private final TestPlugin plugin;

    @EventHandler
    public void onPlayerMove( PlayerMoveEvent event ) {
        BlockPosition toBlock = event.getTo().toBlockPosition();
        if ( !toBlock.equals( event.getFrom().toBlockPosition() ) ) {
            Block block = event.getTo().getWorld().getBlockAt( toBlock );

            event.getPlayer().sendMessage( ChatType.POPUP,
                "§fX: §a" + toBlock.getX() + " §e- §fY: §a" + toBlock.getY() + " §e- §fZ: §a" + toBlock.getZ() + " | " + event.getPlayer().getPing() + " ms",
                "§fWalking on block: §a" + block.getClass() + ":" + ( (io.gomint.server.world.block.Block) block ).getBlockData() );

            Block blockDown = event.getPlayer().getWorld().getBlockAt( event.getPlayer().getLocation().toBlockPosition().add( BlockPosition.DOWN ) );
            if ( Math.random() < 0.5 ) {
                blockDown = blockDown.setType( BlockStone.class );
            } else {
                blockDown = blockDown.setType( BlockWood.class );
            }

            Block finalBlockDown = blockDown;
            this.plugin.getScheduler().schedule( () -> {
                EntityFallingBlock fallingBlock = EntityFallingBlock.create();
                fallingBlock.setBlock( finalBlockDown );
                fallingBlock.spawn( finalBlockDown.getLocation() );

                finalBlockDown.setType( BlockAir.class );
            }, 10, TimeUnit.SECONDS );
        }
    }

}
