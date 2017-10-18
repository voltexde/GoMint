package io.gomint.server.command;

import com.google.common.reflect.ClassPath;
import io.gomint.command.Command;
import io.gomint.plugin.Plugin;
import io.gomint.server.entity.CommandPermission;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.PacketAvailableCommands;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class CommandManager {

    private Map<String, CommandHolder> commands = new ConcurrentHashMap<>();
    private Map<String, Plugin> commandPlugins = new ConcurrentHashMap<>();

    public CommandManager() {
        // Register all internal commands
        try {
            for ( ClassPath.ClassInfo classInfo : ClassPath.from( ClassLoader.getSystemClassLoader() ).getTopLevelClasses( "io.gomint.server.command.internal" ) ) {
                Class<? extends Command> cmdClass = (Class<? extends Command>) classInfo.load();
                Command cmdObj = cmdClass.getConstructor(  ).newInstance(  );
                register( null, cmdObj );
            }
        } catch ( IOException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

    public void register( Plugin plugin, Command commandBuilder ) {
        // Check if command is complete
        if ( commandBuilder.getName() == null ||
                commandBuilder.getDescription() == null) {
            throw new IllegalStateException( "Name or Description can't be null" );
        }

        this.internalRegister( plugin, commandBuilder.getName(), commandBuilder );

        // TODO: Remove once aliases are fixed in 1.2
        if ( commandBuilder.getAlias() != null ) {
            for ( String s : commandBuilder.getAlias() ) {
                this.internalRegister( plugin, s, commandBuilder );
            }
        }
    }

    private void internalRegister( Plugin plugin, String name, Command commandBuilder ) {
        // Check for name collision
        CommandHolder holder = this.commands.get( name );
        if ( holder != null ) {
            // Remap the old command to its fallback
            Plugin originalPlugin = this.commandPlugins.get( name );
            String cmdName;
            if ( originalPlugin != null ) {
                cmdName = originalPlugin.getName() + ":" + name;
            } else {
                cmdName = "gomint:" + name;
            }

            CommandHolder commandHolder = new CommandHolder(
                cmdName,
                holder.getDescription(),
                holder.getAlias(),
                holder.getCommandPermission(),
                holder.getPermission(),
                holder.getExecutor(),
                holder.getOverload()
            );

            this.commands.put( cmdName, commandHolder );
            this.commandPlugins.put( cmdName, originalPlugin );
        }

        // Create a new holder
        holder = new CommandHolder(
            name,
            commandBuilder.getDescription(),
            commandBuilder.getAlias(),
            CommandPermission.NORMAL,
            commandBuilder.getPermission(),
            commandBuilder,
            commandBuilder.getOverload() );

        // Store the command for usage
        this.commands.put( name, holder );
        if ( plugin != null ) {
            this.commandPlugins.put( name, plugin );
        }
    }

    public PacketAvailableCommands createPacket( EntityPlayer player ) {
        List<CommandHolder> holders = new ArrayList<>();
        for ( CommandHolder holder : this.commands.values() ) {
            if ( holder.getPermission() == null || player.hasPermission( holder.getPermission() ) ) {
                holders.add( holder );
            }
        }

        CommandPreprocessor preprocessor = new CommandPreprocessor( holders );
        return preprocessor.getCommandsPacket();
    }

    public Collection<CommandHolder> getCommands() {
        return commands.values();
    }

}
