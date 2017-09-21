package io.gomint.command;

import lombok.Getter;

import java.util.*;

/**
 * @author geNAZt
 * @version 1.0
 *          <p>
 *          This class builds up a command. A command can have defined parameters and permissions
 *          <p>
 *          Quick notes on the API design idea:
 *          <p>
 *          Command cmd = new Command( "test" );
 *          cmd.description("This is just a test command");
 */
@Getter
public class Command {

    private final String name;
    private String description;
    private Map<String, ParamValidator> parameters;
    private final Set<String> alias = new HashSet<>();
    private String permission;
    private CommandExecutor executor;

    /**
     * Construct a new command builder for a command name
     *
     * @param name The base name of the command
     */
    public Command( String name ) {
        this.name = name;
    }

    /**
     * Set the description for this command
     *
     * @param description of the command
     * @return the command currently build
     */
    public Command description( String description ) {
        this.description = description;
        return this;
    }

    /**
     * Set the permission a player needs to execute this command
     *
     * @param permission The permission which the player needs to execute
     * @return the command currently build
     */
    public Command permission( String permission ) {
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
    public Command param( String name, ParamValidator validator ) {
        if ( this.parameters == null ) {
            this.parameters = new HashMap<>();
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
    public Command alias( String alias ) {
        this.alias.add( alias );
        return this;
    }

    /**
     * Set the executor which should handle the execution of this command
     *
     * @param executor of this command
     * @return this command currently build
     */
    public Command executor( CommandExecutor executor ) {
        this.executor = executor;
        return this;
    }

    /**
     * Finally register the command
     */
    public void build() {

    }

}
