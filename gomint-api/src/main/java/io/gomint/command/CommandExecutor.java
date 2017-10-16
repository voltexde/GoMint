package io.gomint.command;

import io.gomint.entity.Player;

import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface CommandExecutor {

    CommandOutput execute( Player player, Map<String, Object> arguments );

}
