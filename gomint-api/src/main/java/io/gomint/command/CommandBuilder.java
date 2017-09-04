package io.gomint.command;

/**
 * @author geNAZt
 * @version 1.0
 *          <p>
 *          This class builds up a command. A Command can have defined sub commands, flags and states
 *          <p>
 *          Quick notes on the API design idea:
 *          <p>
 *          CommandBuilder cmdBuilder = new CommandBuilder( "test" );
 *          cmdBuilder.createSubCommand( "set" ).addParam( "blockPos",
 */
public class CommandBuilder {

    private final String name;

    private String permission;

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

    public CommandBuilder param( String name, ParamValidator validator ) {
        return this;
    }

}
