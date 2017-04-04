/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.plugin;

import io.gomint.plugin.PluginVersion;
import io.gomint.plugin.StartupPriority;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.File;
import java.util.Set;

/**
 * @author BlackyPaw
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
@ToString
public class PluginMeta {

    private final String name;
    private final PluginVersion version;
    private final StartupPriority priority;
    private final Set<String> depends;
    private final Set<String> softDepends;
    private final String mainClass;
    private final File pluginFile;

}
