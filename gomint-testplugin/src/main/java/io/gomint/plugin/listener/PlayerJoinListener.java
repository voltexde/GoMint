package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.gui.CustomForm;
import io.gomint.gui.FormListener;
import io.gomint.gui.FormResponse;
import io.gomint.gui.element.Dropdown;
import io.gomint.inventory.item.*;
import io.gomint.inventory.item.data.DyeType;
import io.gomint.plugin.TestPlugin;
import lombok.RequiredArgsConstructor;

import java.awt.*;
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
        event.getPlayer().getInventory().setItem( 0, ItemCake.create( 1 ) );

        event.getPlayer().getInventory().setItem( 1, ItemCarrot.create( 64 ) );
        event.getPlayer().getInventory().setItem( 2, ItemGoldenCarrot.create( 64 ) );

        event.getPlayer().getInventory().setItem( 3, ItemDiamond.create( 64 ) );
        event.getPlayer().getInventory().setItem( 4, ItemStick.create( 1 ) );

        event.getPlayer().getInventory().setItem( 5, ItemLog.create( 5 ) );

        event.getPlayer().getInventory().setItem( 6, ItemElytra.create( 1 ) );

        // Create red leather armor
        ItemLeatherHelmet redHelmet = ItemLeatherHelmet.create( 1 );
        redHelmet.setColor( new Color( 193, 19, 25 ) );
        event.getPlayer().getInventory().setItem( 7, redHelmet );

        // Create lapis
        ItemDye dye = ItemDye.create( 32 );
        dye.setDyeType( DyeType.LAPIS_LAZULI );
        event.getPlayer().getInventory().setItem( 8, dye );

        event.getPlayer().setLevel( 67 );

        CustomForm settings = CustomForm.create( "GoMint" );
        settings.addLabel( "General" );
        settings.addToggle( "show-position", "Show current position", true );
        FormListener<FormResponse> response = event.getPlayer().addSettingsForm( settings );
        response.onResponse( formResponse -> plugin.getLogger().info( String.valueOf( formResponse.getToogle( "show-position" ) ) ) );

        this.plugin.getScheduler().schedule( new Runnable() {
            @Override
            public void run() {
                CustomForm dropboxTest = CustomForm.create( "Test" );
                Dropdown dropdown = dropboxTest.createDropdown( "test-drop", "Dropdown" );
                dropdown.addOption( "Test" );
                dropdown.addOption( "Test 1" );
                FormListener<FormResponse> responseFormListener = event.getPlayer().showForm( dropboxTest );
                responseFormListener.onResponse( new Consumer<FormResponse>() {
                    @Override
                    public void accept( FormResponse formResponse ) {
                        System.out.println( formResponse.getDropbox( "test-drop" ) );
                    }
                } );
            }
        }, 1, TimeUnit.SECONDS );

        this.plugin.getBossBarOrb().getBossBar().addPlayer( event.getPlayer() );
    }

}
