package io.gomint.plugin.command;

import io.gomint.command.Command;
import io.gomint.command.CommandOutput;
import io.gomint.command.annotation.Description;
import io.gomint.command.annotation.Name;
import io.gomint.command.annotation.Overload;
import io.gomint.command.annotation.Parameter;
import io.gomint.command.validator.TargetValidator;
import io.gomint.entity.EntityPlayer;
import io.gomint.entity.monster.EntitySlime;

import java.util.Map;

/**
 * @author geNAZt
 */
@Name( "spawn" )
@Description( "Test command for entity spawn" )
@Overload( {
    @Parameter( name = "target", validator = TargetValidator.class )
})
public class CommandSpawn extends Command {

    @Override
    public CommandOutput execute( EntityPlayer player, String alias, Map<String, Object> arguments ) {
        EntitySlime entitySlime = EntitySlime.create();
        entitySlime.spawn( player.getLocation() );

        return new CommandOutput();
    }

}
