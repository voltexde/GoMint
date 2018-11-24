/*
 * Copyright (c) 2018 Gomint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class CommandLineHolder {

    @Getter private final String[] args;

}
