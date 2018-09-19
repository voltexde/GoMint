/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public class BlockIdentifier {

    private final String blockId;
    private final short data;

    @Override
    public int hashCode() {
        return this.blockId.hashCode() + this.data;
    }

    public long longHashCode() {
        return (long) this.blockId.hashCode() << 32 | this.data;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof BlockIdentifier ) {
            BlockIdentifier other = (BlockIdentifier) obj;
            return other.blockId.equals( this.blockId ) && other.data == this.data;
        }

        return false;
    }

}
