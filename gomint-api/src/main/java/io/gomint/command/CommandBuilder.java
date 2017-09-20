package io.gomint.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 *          <p>
 *          This class builds up a command. A CommandExecutor can have defined sub commands, flags and states
 *          <p>
 *          Quick notes on the API design idea:
 *          <p>
 *          CommandBuilder cmdBuilder = new CommandBuilder( "test" );
 *          cmdBuilder.createSubCommand( "set" ).addParam( "blockPos",
 */
public class CommandBuilder {

    private final String name;
    private final Map<String, ParamValidator> parameters = new HashMap<>();
    private final List<String> alias = new ArrayList<>();
    private String permission;
    private Map<String, CommandBuilder> subComamnds = new HashMap<>();

    /**
     * Construct a new command builder for a command name
     *
     * @param name The base name of the command
     */
    public CommandBuilder( String name ) {
        this.name = name;
    }

    /**
     * Set the permission a player needs to execute this command
     *
     * @param permission The permission which the player needs to execute
     * @return the command currently build
     */
    public CommandBuilder permission( String permission ) {
        this.permission = permission;
        return this;
    }

    /**
     * Add a param to the command. Params are passed to their validators when the command gets
     * executed.
     *
     * @param name of the parameter
     * @param validator which should decide if the parameter is valid
     * @return the command currently build
     */
    public CommandBuilder param( String name, ParamValidator validator ) {
        // Only valid when no subcommands have been defined
        if ( this.subComamnds.size() > 0 ) {
            throw new IllegalStateException( "Parameters can only be used when no subcommands have been defined" );
        }

        this.parameters.put( name, validator );
        return this;
    }

    /**
     * Add an alias to the command
     *
     * @param alias which should be added
     * @return the command currently build
     */
    public CommandBuilder alias( String alias ) {
        this.alias.add( alias );
        return this;
    }

    /**
     * Create a sub command
     *
     * @param name of the sub command
     * @return new command builder for the sub command
     */
    public CommandBuilder createSubCommand( String name ) {
        // This only works when we have no parameters
        if ( this.parameters.size() > 0 ) {
            throw new IllegalStateException( "You can only use subcommands when no parameters have been defined" );
        }

        CommandBuilder commandBuilder = new CommandBuilder( name );
        this.subComamnds.put( name, commandBuilder );
        return commandBuilder;
    }

}
