/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.plugin.config;

import com.blackypaw.simpleconfig.SimpleConfig;
import com.blackypaw.simpleconfig.annotation.Comment;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@Getter
public class Config extends SimpleConfig {

    @Comment("Enable the testing of the chunk generator?")
    private boolean enableChunkGeneration = true;

}
