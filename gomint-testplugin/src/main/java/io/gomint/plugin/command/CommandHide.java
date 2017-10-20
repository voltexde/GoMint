/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.plugin.command;

import io.gomint.command.Command;
import io.gomint.command.CommandOutput;
import io.gomint.command.annotation.Description;
import io.gomint.command.annotation.Name;
import io.gomint.command.annotation.Overload;
import io.gomint.command.annotation.Parameter;
import io.gomint.command.validator.TargetValidator;
import io.gomint.entity.EntityPlayer;

import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
@Name( "hide" )
@Description( "Hide other players" )
@Overload( {
    @Parameter( name = "target", validator = TargetValidator.class )
} )
public class CommandHide extends Command {

    @Override
    public CommandOutput execute( EntityPlayer player, String alias, Map<String, Object> arguments ) {
        CommandOutput output = new CommandOutput();

        EntityPlayer hide = (EntityPlayer) arguments.get( "target" );
        if ( !player.equals( hide ) ) {
            player.hidePlayer( hide );
            output.success( "EntityPlayer %%s has been hidden", hide.getName() );
        } else {
            output.fail( "You can't hide yourself" );
        }

        return output;
    }

}
