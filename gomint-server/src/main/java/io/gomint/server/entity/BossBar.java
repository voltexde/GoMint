/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.entity.EntityPlayer;
import io.gomint.server.network.packet.PacketBossBar;
import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class BossBar implements io.gomint.entity.BossBar {

    private final Entity entity;

    @Override
    public void addPlayer( EntityPlayer player ) {
        PacketBossBar packet = new PacketBossBar();
        packet.setEntityId( this.entity.getEntityId() );
        packet.setType( PacketBossBar.Type.SHOW );
        ( (io.gomint.server.entity.EntityPlayer) player ).getConnection().addToSendQueue( packet );
    }

    @Override
    public void removePlayer( EntityPlayer player ) {
        PacketBossBar packet = new PacketBossBar();
        packet.setEntityId( this.entity.getEntityId() );
        packet.setType( PacketBossBar.Type.HIDE );
        ( (io.gomint.server.entity.EntityPlayer) player ).getConnection().addToSendQueue( packet );
    }

    @Override
    public void setTitle( String title ) {
        this.entity.setNameTag( title );
    }

    @Override
    public String getTitle() {
        return this.entity.getNameTag();
    }

}
