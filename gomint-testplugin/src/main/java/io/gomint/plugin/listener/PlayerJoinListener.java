package io.gomint.plugin.listener;

import io.gomint.GoMint;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.inventory.item.*;
import io.gomint.world.World;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerJoinListener implements EventListener {

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        event.getPlayer().getInventory().setItem( 0, ItemBeetrootSeeds.create( 12 ) );
        event.getPlayer().getInventory().setItem( 1, ItemFarmland.create( 12 ) );

        ItemBucket itemBucket = ItemBucket.create( 1 );
        itemBucket.setContent( ItemBucket.Content.WATER );

        event.getPlayer().getInventory().setItem( 2, itemBucket );
        event.getPlayer().getInventory().setItem( 3, ItemCookedBeef.create( 2 ) );

        event.getPlayer().getInventory().setItem( 4, ItemStoneSword.create( 1 ) );
        event.getPlayer().getInventory().setItem( 5, ItemStonePickaxe.create( 1 ) );
        event.getPlayer().getInventory().setItem( 6, ItemSandstone.create( 64 ) );

        event.getPlayer().getArmorInventory().setBoots( ItemIronBoots.create( 1 ) );

        // Set health to 2
        event.getPlayer().setHealth( 2 );

        // Get second world
        World world = GoMint.instance().getWorld( "world1" );
        event.getPlayer().teleport( world.getSpawnLocation() );
    }

}
