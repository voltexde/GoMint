package io.gomint.server.config;

import com.blackypaw.simpleconfig.SimpleConfig;
import com.blackypaw.simpleconfig.annotation.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@NoArgsConstructor
@Getter
public class WorldConfig extends SimpleConfig {

    @Comment( "How many blocks should we update per tick using random reasons" )
    private int randomUpdatesPerTick = 5;

}
