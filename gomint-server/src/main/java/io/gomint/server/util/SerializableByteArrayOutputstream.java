/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import java.io.ByteArrayOutputStream;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SerializableByteArrayOutputstream extends ByteArrayOutputStream {

    /**
     * Return the stored byte for the index
     *
     * @param i The index we should lookup
     * @return The stored byte without any copy
     */
    public byte getByte( int i ) {
        return buf[i];
    }

}
