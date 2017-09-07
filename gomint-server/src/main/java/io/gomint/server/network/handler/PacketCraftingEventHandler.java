package io.gomint.server.network.handler;

import io.gomint.inventory.ItemStack;
import io.gomint.server.crafting.Recipe;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketCraftingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketCraftingEventHandler implements PacketHandler<PacketCraftingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger( PacketCraftingEventHandler.class );

    @Override
    public void handle( PacketCraftingEvent packet, long currentTimeMillis, PlayerConnection connection ) {
        System.out.println( packet );

        // Get the recipe based on its id
        Recipe recipe = connection.getEntity().getWorld().getServer().getRecipeManager().getRecipe( packet.getRecipeId() );
        if ( recipe == null ) {
            // Resend inventory and call it a day
            for ( ItemStack itemStack : connection.getEntity().getCraftingResultInventory().getContents() ) {
                connection.getEntity().getInventory().addItem( itemStack );
            }

            connection.getEntity().getInventory().sendContents( connection );
            return;
        }

        // Generate lists of output and input
        List<ItemStack> packetOutput = new ArrayList<>( Arrays.asList( packet.getOutput() ) );

        // Generate a output stack for compare
        Collection<ItemStack> output = recipe.createResult();

        // Due to a bug in MC:PE it can happen that the recipe id is shit
        if ( output.size() != packet.getOutput().length) {
            LOGGER.debug( "Output size does not match up" );
            recipe = connection.getEntity().getWorld().getServer().getRecipeManager().getRecipe( packetOutput );
        } else {
            Iterator<ItemStack> recipeSide = output.iterator();
            Iterator<ItemStack> packetSide = packetOutput.iterator();

            while ( recipeSide.hasNext() ) {
                ItemStack recipeOutput = recipeSide.next();
                ItemStack packetSingleOutput = packetSide.next();

                if ( !recipeOutput.equals( packetSingleOutput ) ) {
                    LOGGER.debug( "Packet wanted to get a recipe with different output" );
                    recipe = connection.getEntity().getWorld().getServer().getRecipeManager().getRecipe( packetOutput );
                    output = recipe.createResult();
                    break;
                }
            }
        }

        // Crafting types:
        // 0 => Small crafting window inside of the player inventory

        // If the crafting window is small you can't craft bigger recipes
        if ( packet.getRecipeType() == 0 && connection.getEntity().getCraftingResultInventory().getSize() > 4 ) {
            // Resend inventory and call it a day
            connection.getEntity().getInventory().sendContents( connection );
            return;
        }

        // Now we have to look if we have the correct items
        ItemStack[] inputItems = connection.getEntity().getCraftingResultInventory().getContents();
        boolean craftable = true;
        for ( ItemStack recipeWanted : recipe.getIngredients() ) {
            boolean found = false;
            for ( ItemStack input : inputItems ) {
                if ( recipeWanted.getMaterial() == input.getMaterial() &&
                        ( recipeWanted.getData() == -1 || recipeWanted.getData() == input.getData() ) ) {
                    found = true;
                    break;
                }
            }

            if ( !found ) {
                craftable = false;
                break;
            }
        }

        // Calculate the items we need to consume
        List<ItemStack> available = new ArrayList<>( Arrays.asList( inputItems ) );
        List<ItemStack> consume = new ArrayList<>();

        // We need to consume every item in there
        for ( ItemStack neededToConsume : packet.getInput() ) {
            boolean didConsume = false;
            for ( ItemStack canConsume : available ) {
                if ( neededToConsume.getMaterial() == canConsume.getMaterial() &&
                        ( neededToConsume.getData() == -1 || neededToConsume.getData() == canConsume.getData() ) ) {
                    if ( canConsume.getAmount() > 0 ) {
                        canConsume.setAmount( canConsume.getAmount() - 1 );

                        boolean didFindAlreadyConsumed = false;
                        for ( ItemStack alreadyConsumed : consume ) {
                            if ( alreadyConsumed.equals( canConsume ) ) {
                                alreadyConsumed.setAmount( alreadyConsumed.getAmount() + 1 );
                                didFindAlreadyConsumed = true;
                                break;
                            }
                        }

                        if ( !didFindAlreadyConsumed ) {
                            ItemStack canConsumeClone = canConsume.clone();
                            canConsumeClone.setAmount( 1 );
                            consume.add( canConsumeClone );
                        }

                        didConsume = true;
                        break;
                    }
                }
            }

            if ( !didConsume ) {
                craftable = false;
                break;
            }
        }

        // We can craft this
        if ( craftable ) {
            // TODO: Event to cancel the crafting if needed
            for ( ItemStack itemStack : output ) {
                if ( !connection.getEntity().getInventory().addItem( itemStack ) ) {
                    // Drop it?

                }
            }

            // Reset the inventory
            connection.getEntity().getCraftingResultInventory().clear();
        } else {
            // We can't craft => reset inventory
            for ( ItemStack inputItem : inputItems ) {
                connection.getEntity().getInventory().addItem( inputItem );
            }

            connection.getEntity().getInventory().sendContents( connection );
            connection.getEntity().getCraftingResultInventory().clear();
        }
    }

}
