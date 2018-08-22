package io.gomint.server.command.internal;

import io.gomint.GoMint;
import io.gomint.command.Command;
import io.gomint.command.CommandOutput;
import io.gomint.command.CommandSender;
import io.gomint.command.PlayerCommandSender;
import io.gomint.command.annotation.*;
import io.gomint.command.validator.TextValidator;
import io.gomint.server.entity.EntityPlayer;

import java.util.Map;

/**
 * @author Kaooot
 * @version 1.0
 */
@Name( "say" )
@Description( "Sends a message in the chat to other players." )
@Permission( "gomint.command.say" )
@Overload( {
    @Parameter( name = "message", validator = TextValidator.class )
} )
public class SayCommand extends Command {

    @Override
    public CommandOutput execute( CommandSender commandSender, String alias, Map<String, Object> arguments ) {
        CommandOutput output = new CommandOutput();

        String message = (String) arguments.get( "message" );

        if( commandSender instanceof PlayerCommandSender ) {
            EntityPlayer player = (EntityPlayer) commandSender;

            GoMint.instance().getPlayers().forEach( players -> players.sendMessage( "[" + player.getName() + "] " + message ) );
        } else {
            GoMint.instance().getPlayers().forEach( player -> player.sendMessage( "[Server] " + message ) );
        }
        return output;
    }
}
