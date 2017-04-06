package io.gomint.server.entity;

import io.gomint.server.network.packet.PacketAdventureSettings;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class AdventureSettings {

    private boolean canDestroyBlock = true;
    private boolean autoJump = true;
    private boolean canFly = false;
    private boolean flying = false;
    private boolean noclip = false;
    private boolean noPvP = false;
    private boolean noPvM = false;
    private boolean noMvP = false;

    private EntityPlayer player;

    /**
     * Adventure settings which control client behaviour
     *
     * @param entityPlayer The player for which this settings are
     */
    AdventureSettings( EntityPlayer entityPlayer ) {
        this.player = entityPlayer;
    }

    /**
     * Send the setting to the client
     */
    public void update() {
        PacketAdventureSettings adventureSettingsPacket = new PacketAdventureSettings();

        int flags = 0;
        if ( !this.canDestroyBlock ) {
            flags |= 0x01;              // Immutable World (Remove hit markers client-side).
        }

        if ( this.noPvP ) {
            flags |= 0x02;              // No PvP (Remove hit markers client-side).
        }

        if ( this.noPvM ) {
            flags |= 0x04;              // No PvM (Remove hit markers client-side).
        }

        if ( this.noMvP ) {
            flags |= 0x08;
        }

        if ( this.autoJump ) {
            flags |= 0x20;
        }

        if ( this.canFly ) {
            flags |= 0x40;
        }

        if ( this.noclip ) {
            flags |= 0x80;              // No clip
        }

        if ( this.flying ) {
            flags |= 0x200;             // Set flying
        }

        adventureSettingsPacket.setFlags( flags );
        adventureSettingsPacket.setUserPermission( this.player.isOp() ? 1 : 0 );
        this.player.getConnection().send( adventureSettingsPacket );
    }

}
