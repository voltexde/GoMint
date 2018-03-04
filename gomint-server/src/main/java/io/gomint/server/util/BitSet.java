/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 *
 * This class does read the bit representation of a byte from left to right. For example:
 *
 * Having 00001000 will give you a byte with value of 8 but the new palette changes introduce a byte which is split.
 * First part is a boolean (0), the rest is a LE! byte. In this case 0001000
 */
@RequiredArgsConstructor
public class BitSet {

    private final byte value;
    private byte currentPos = 8;

}
