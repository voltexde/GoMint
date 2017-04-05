package io.gomint.server.entity;

import io.gomint.server.network.packet.PacketAdventureSettings;
import io.gomint.world.Gamemode;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class AdventureSettings {

    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;

    private boolean canDestroyBlock = true;
    private boolean autoJump = true;
    private boolean canFly = false;
    private boolean flying = false;
    private boolean noclip = false;
    private boolean noPvP = false;
    private boolean noPvM = false;
    private boolean noMvP = false;

    private EntityPlayer player;

    public AdventureSettings( EntityPlayer entityPlayer ) {
        this.player = entityPlayer;
    }

    public void update() {
        System.out.println( this );

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
        adventureSettingsPacket.setUserPermission( this.player.isOp() ? PERMISSION_OPERATOR : PERMISSION_NORMAL );
        this.player.getConnection().send( adventureSettingsPacket );
    }

}
