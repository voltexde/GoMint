/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.scoreboard;

import io.gomint.server.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Data
public class ScoreboardDisplay {

    private final Scoreboard scoreboard;
    private final String objectiveName;
    private String displayName;

    public void addEntity( Entity entity, int score ) {
        this.scoreboard.addEntity( entity, this.objectiveName, score );
    }

    public void addString( String name, int score ) {
        this.scoreboard.addString( name, this.objectiveName, score );
    }

}
