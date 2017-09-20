package io.gomint.server.network.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author geNAZt
 */
@AllArgsConstructor
@Getter
public class CommandData {

    private String name;
    private String description;
    private byte flags;
    private byte perission;
    private int aliasIndex;
    private List<Parameter> parameters;

    @AllArgsConstructor
    @Getter
    public class Parameter {
        private String name;
        private int type;
        private boolean optional;
    }

}
