/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.jni.hash;

import io.netty.buffer.ByteBuf;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author geNAZt
 * @version 1.0
 */
public class JavaHash implements Hash {

    private final MessageDigest digest;

    public JavaHash() {
        try {
            digest = MessageDigest.getInstance( "SHA-256" );
        } catch ( NoSuchAlgorithmException e ) {
            // Can't possibly happen as SHA-256 is required by the MessageDigest class to be present.
            throw new AssertionError( e );
        }
    }

    @Override
    public void update( ByteBuf buf ) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes( bytes );
        digest.update( bytes );
    }

    @Override
    public byte[] digest() {
        return digest.digest();
    }

    @Override
    public void free() {
        // No-op.
    }

    @Override
    public void reset() {
        digest.reset();
    }

}
