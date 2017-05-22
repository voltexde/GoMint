package io.gomint.server.network.handler;

import io.gomint.inventory.ItemStack;
import io.gomint.server.crafting.Recipe;
import io.gomint.server.inventory.transaction.CraftingTransaction;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketCraftingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketCraftingEventHandler implements PacketHandler<PacketCraftingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger( PacketCraftingEventHandler.class );

    @Override
    public void handle( PacketCraftingEvent packet, long currentTimeMillis, PlayerConnection connection ) {
        // Get the recipe based on its id
        Recipe recipe = connection.getEntity().getWorld().getServer().getRecipeManager().getRecipe( packet.getRecipeId() );
        if ( recipe == null ) {
            // Resend inventory and call it a day
            connection.getEntity().getInventory().sendContents( connection );
            return;
        }

        // Generate a output stack for compare
        Collection<ItemStack> output = recipe.createResult();

        // Due to a bug in MC:PE it can happen that the recipe id is shit
        if ( output.size() != packet.getOutput().size() ) {
            LOGGER.debug( "Output size does not match up" );
            recipe = connection.getEntity().getWorld().getServer().getRecipeManager().getRecipe( packet.getOutput() );
        } else {
            Iterator<ItemStack> recipeSide = output.iterator();
            Iterator<ItemStack> packetSide = packet.getOutput().iterator();

            while ( recipeSide.hasNext() ) {
                ItemStack recipeOutput = recipeSide.next();
                ItemStack packetOutput = packetSide.next();

                if ( !recipeOutput.equals( packetOutput ) ) {
                    LOGGER.debug( "Packet wanted to get a recipe with different output" );
                    recipe = connection.getEntity().getWorld().getServer().getRecipeManager().getRecipe( packet.getOutput() );
                    break;
                }
            }
        }

        // Crafting types:
        // 0 => Small crafting window inside of the player inventory

        // If the crafting window is small you can't craft bigger recipes
        if ( packet.getRecipeType() == 0 && recipe.getIngredients().size() > 4 ) {
            // Resend inventory and call it a day
            connection.getEntity().getInventory().sendContents( connection );
            return;
        }

        // Patch up a bug where the input side is empty when in desktop gui mode
        if ( packet.getInput().size() == 0 ) {
            packet.getInput().addAll( recipe.getIngredients() );
        }

        // Now we have to look if we have the correct items
        boolean craftable = true;
        for ( ItemStack recipeWanted : recipe.getIngredients() ) {
            boolean found = false;
            for ( ItemStack input : packet.getInput() ) {
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
        connection.getEntity().getTransactions().canExecute();
        List<ItemStack> available = new ArrayList<>( connection.getEntity().getTransactions().getHaveItems() );
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


            // Manipulate the running transaction
            ItemStack recipeOutput = recipe.createResult().iterator().next();
            for ( ItemStack itemStack : consume ) {
                connection.getEntity().getTransactions().addTransaction( new CraftingTransaction( recipeOutput, itemStack, currentTimeMillis ) );
                recipeOutput = null;
            }

            // Check if we can execute transaction
            connection.getEntity().getTransactions().tryExecute( currentTimeMillis );
        } else {
            // We can't craft => reset inventory
            connection.getEntity().getInventory().sendContents( connection );
        }
    }

}
