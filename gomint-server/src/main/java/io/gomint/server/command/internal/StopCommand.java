package io.gomint.server.command.internal;

import io.gomint.GoMint;
import io.gomint.command.Command;
import io.gomint.command.CommandOutput;
import io.gomint.command.annotation.Description;
import io.gomint.command.annotation.Name;
import io.gomint.command.annotation.Permission;
import io.gomint.entity.EntityPlayer;
import io.gomint.server.GoMintServer;

import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
@Name( "stop" )
@Description( "Stops the GoMint server" )
@Permission( "gomint.commands.stop" )
public class StopCommand extends Command {

    @Override
    public CommandOutput execute( EntityPlayer player, String alias, Map<String, Object> arguments ) {
        ( (GoMintServer) GoMint.instance() ).shutdown();
        return new CommandOutput().success( "§7[§aSYSTEM§7] §fServer will be stopped" );
    }

}
