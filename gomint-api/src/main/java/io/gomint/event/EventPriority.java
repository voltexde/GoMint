/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Fabian
 * @version 1.0
 *
 * Importance of the {@link EventHandler}. When executing an Event, the handlers
 * are called in order of their Priority.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public enum EventPriority {

    LOWEST( (byte) -64 ),
    LOW( (byte) -32 ),
    NORMAL( (byte) 0 ),
    HIGH( (byte) 32 ),
    HIGHEST( (byte) 64 );

	private byte order;

    EventPriority( final byte value ) {
        this.order = value;
    }

    public byte getValue() {
        return this.order;
    }
}
