package io.gomint.command;

import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface ParamValidator {

    /**
     * Get the type of this param.
     *
     * @return type of param
     */
    ParamType getType();

    /**
     * Does this validator have values which should be sent with the command. This is
     * mostly used by enum params which define a set of values for selection.
     *
     * @return true when there is data to be sent with this parameter, false when not
     */
    boolean hasValues();

    /**
     * Get the values when {@link #hasValues()} is true.
     *
     * @return sorted list of values
     */
    List<String> values();

    /**
     * Validates given input
     *
     * @param input from the command
     * @return non null object of validation on success (string for example) or null when validation failed
     */
    Object validate( List<String> input );

    /**
     * Is this param optional?
     *
     * @return true when its optional, false when not
     */
    boolean isOptional();

    /**
     * Return how much parts of the command this validator plans to consume
     *
     * @return amount of parts which should be consumed
     */
    int consumesParts();

}
