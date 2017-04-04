/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint;

/**
 * @author BlackyPaw
 * @author geNAZt
 * @version 1.0
 */
public interface GoMint {

    /**
     * Get the server's message of the day (MOTD)
     *
     * @return The server's current MOTD
     */
    String getMotd();

    /**
     * Sets the server's message of the day (MOTD)
     *
     * @param motd The MOTD to be set
     */
    void setMotd( String motd );

}
