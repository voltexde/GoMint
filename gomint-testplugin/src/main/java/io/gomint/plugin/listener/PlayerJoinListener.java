package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.gui.CustomForm;
import io.gomint.gui.FormListener;
import io.gomint.gui.FormResponse;
import io.gomint.gui.Modal;
import io.gomint.inventory.item.*;
import io.gomint.plugin.TestPlugin;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements EventListener {

    private final TestPlugin plugin;

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

        event.getPlayer().getInventory().setItem( 7, ItemFishingRod.create( 1 ) );
        event.getPlayer().getInventory().setItem( 8, ItemBow.create( 1 ) );
        event.getPlayer().getInventory().setItem( 9, ItemArrow.create( 64 ) );
        event.getPlayer().getInventory().setItem( 10, ItemFlintAndSteel.create( 1 ) );

        event.getPlayer().getArmorInventory().setBoots( ItemIronBoots.create( 1 ) );
    }

}
