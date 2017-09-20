package io.gomint.server.command;

import io.gomint.command.CommandExecutor;
import io.gomint.command.ParamValidator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * @author geNAZt
 */
@AllArgsConstructor
@Getter
public class CommandHolder {

    private String name;
    private Set<String> alias;

    private String permission;
    private CommandExecutor executor;
    private Map<String, ParamValidator> validators;

}
