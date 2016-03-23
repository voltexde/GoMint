/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * We don't use the AWT Variant here because it used too much memory.
 *
 * @author geNAZt
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public class Color {

    private byte r;
    private byte g;
    private byte b;

}
