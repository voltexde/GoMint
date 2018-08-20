/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.jni.hash;

import io.netty.buffer.ByteBuf;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface Hash {

    void update(ByteBuf buf);

    byte[] digest();

    void free();

    void reset();

}
