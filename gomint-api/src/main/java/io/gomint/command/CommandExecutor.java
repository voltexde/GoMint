package io.gomint.command;

import io.gomint.entity.Player;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class CommandExecutor {

    abstract void execute( Player player, Object... arguments );

}
