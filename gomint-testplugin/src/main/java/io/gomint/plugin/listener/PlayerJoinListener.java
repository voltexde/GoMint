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

        event.getPlayer().getArmorInventory().setBoots( ItemIronBoots.create( 1 ) );

        // Set health to 2
        event.getPlayer().setHealth( 2 );

        this.plugin.getScheduler().schedule( new Runnable() {
            @Override
            public void run() {
                CustomForm customForm = CustomForm.create( "GoMint Feedback" );
                customForm.addSlider( "niceLevel", "How nice is GoMint?", 0, 100, 1, 100 );
                customForm.addInputField( "reason", "Why is Gomint so nice?", "Answer here", "don't know bud" );
                customForm.addToggle( "useAgain", "10/10 use again?", true );
                FormListener<FormResponse> listener = event.getPlayer().showForm( customForm );
                listener.onResponse( new Consumer<FormResponse>() {
                    @Override
                    public void accept( FormResponse response ) {
                        event.getPlayer().sendMessage( "Answer #1 (How nice is GoMint?): " + response.getSlider( "niceLevel" ) );
                    }
                } );
            }
        }, 2, TimeUnit.SECONDS );
    }

}
