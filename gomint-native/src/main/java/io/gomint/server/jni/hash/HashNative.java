/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.jni.hash;

/**
 * @author geNAZt
 * @version 1.0
 */
public class HashNative {

    static native long init();

    static native void update( long ctx, long in, int length );

    static native byte[] digest( long ctx );

    static native void free( long ctx );

    static native void reset( long ctx );

}
