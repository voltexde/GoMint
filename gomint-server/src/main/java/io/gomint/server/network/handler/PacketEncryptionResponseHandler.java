/*
 *  Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 *  This code is licensed under the BSD license found in the
 *  LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.handler;

import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.PlayerConnectionState;
import io.gomint.server.network.packet.PacketEncryptionResponse;
import io.gomint.server.network.packet.PacketPlayState;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketEncryptionResponseHandler implements PacketHandler<PacketEncryptionResponse> {

    @Override
    public void handle( PacketEncryptionResponse packet, long currentTimeMillis, PlayerConnection connection ) {
        connection.setState( PlayerConnectionState.RESOURCE_PACK );
        connection.sendPlayState( PacketPlayState.PlayState.LOGIN_SUCCESS );
        connection.sendResourcePacks();
    }

}
