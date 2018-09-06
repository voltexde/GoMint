package io.gomint.server.entity;

import io.gomint.server.registry.SkipRegister;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@SkipRegister
@Data
public class EntityLink {

    private long from;
    private long to;
    private byte unknown1;
    private byte unknown2;

}
