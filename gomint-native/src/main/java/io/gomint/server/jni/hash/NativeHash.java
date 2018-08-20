/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.jni.hash;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

/**
 * @author geNAZt
 * @version 1.0
 */
public class NativeHash implements Hash {

    private final long ctx;
    private boolean freed = false;

    public NativeHash() {
        this.ctx = HashNative.init();
    }

    @Override
    public void update( ByteBuf buf ) {
        // Smoke tests
        checkState();
        buf.memoryAddress();

        // Update the digest
        HashNative.update( this.ctx, buf.memoryAddress() + buf.readerIndex(), buf.readableBytes() );

        // Go to the end of the buffer, all bytes would of been read
        buf.readerIndex( buf.writerIndex() );
    }

    @Override
    public byte[] digest() {
        checkState();
        return HashNative.digest( this.ctx );
    }

    @Override
    public void free() {
        checkState();
        HashNative.free( this.ctx );
        this.freed = true;
    }

    @Override
    public void reset() {
        HashNative.reset( this.ctx );
    }

    private void checkState() {
        Preconditions.checkState( !this.freed, "Already freed" );
    }

}
