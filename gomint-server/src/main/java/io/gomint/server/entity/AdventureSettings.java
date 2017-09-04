package io.gomint.server.entity;

import io.gomint.command.CommandPermission;
import io.gomint.player.PlayerPermission;
import io.gomint.server.network.packet.PacketAdventureSettings;
import io.gomint.server.util.EnumConnectors;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class AdventureSettings {

    // Permissions
    private CommandPermission commandPermission = CommandPermission.NORMAL;
    private PlayerPermission playerPermission = PlayerPermission.MEMBER;

    // Settings

    // Flag 1
    private boolean worldImmutable = false; // 0x01
    private boolean noPvP = false;          // 0x02
    private boolean autoJump = true;        // 0x20
    private boolean canFly = false;         // 0x40
    private boolean noclip = false;         // 0x80
    private boolean worldBuilder = false;   // 0x100
    private boolean flying = false;         // 0x200
    private boolean muted = false;          // 0x400

    // Flag 2
    private boolean buildAndMine = true;        // 0x01
    private boolean useDoorsAndSwitches = true; // 0x02
    private boolean openContainers = true;      // 0x04
    private boolean attackPlayers = true;       // 0x08
    private boolean attackMobs = true;          // 0x10
    private boolean operator = false;           // 0x20
    private boolean teleport = false;           // 0x80

    private EntityPlayer player;

    /**
     * Adventure settings which control client behaviour
     *
     * @param entityPlayer The player for which this settings are
     */
    public AdventureSettings( EntityPlayer entityPlayer ) {
        this.player = entityPlayer;
    }

    public AdventureSettings( int flags, int flags2 ) {
        // Flag 1
        this.worldImmutable = ( flags & 1 ) != 0;
        this.noPvP = ( flags & ( 1 << 1 ) ) != 0;
        this.autoJump = ( flags & ( 1 << 5 ) ) != 0;
        this.canFly = ( flags & ( 1 << 6 ) ) != 0;
        this.noclip = ( flags & ( 1 << 7 ) ) != 0;
        this.worldBuilder = ( flags & ( 1 << 8 ) ) != 0;
        this.flying = ( flags & ( 1 << 9 ) ) != 0;
        this.muted = ( flags & ( 1 << 10 ) ) != 0;

        // Flag 2
        this.buildAndMine = ( flags2 & 1 ) != 0;
        this.useDoorsAndSwitches = ( flags2 & ( 1 << 1 ) ) != 0;
        this.openContainers = ( flags2 & ( 1 << 2 ) ) != 0;
        this.attackPlayers = ( flags2 & ( 1 << 3 ) ) != 0;
        this.attackMobs = ( flags2 & ( 1 << 4 ) ) != 0;
        this.operator = ( flags2 & ( 1 << 5 ) ) != 0;
        this.teleport = ( flags2 & ( 1 << 7 ) ) != 0;
    }

    /**
     * Send the setting to the client
     */
    public void update() {
        PacketAdventureSettings adventureSettingsPacket = new PacketAdventureSettings();

        // Flags 1
        int flags = 0;
        if ( this.worldImmutable ) {
            flags |= 0x01;              // Immutable World (Remove hit markers client-side).
        }

        if ( this.noPvP ) {
            flags |= 0x02;              // No PvP (Remove hit markers client-side).
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

        if ( this.worldBuilder ) {      // TODO: Find out what world builder is
            flags |= 0x100;
        }

        if ( this.flying ) {
            flags |= 0x200;             // Set flying
        }

        if ( this.muted ) {
            flags |= 0x400;
        }

        // Flags 2
        int flags2 = -1;
        if ( this.buildAndMine ) {
            flags2 |= 0x01;
        }

        if ( this.useDoorsAndSwitches ) {
            flags2 |= 0x02;
        }

        if ( this.openContainers ) {
            flags2 |= 0x04;
        }

        adventureSettingsPacket.setFlags( flags );
        adventureSettingsPacket.setFlags2( flags2 );
        adventureSettingsPacket.setCommandPermission( EnumConnectors.COMMANDPERMISSION_CONNECTOR.convert( this.commandPermission ).getId() );
        adventureSettingsPacket.setPlayerPermission( EnumConnectors.PLAYERPERMISSION_CONNECTOR.convert( this.playerPermission ).getId() );
        adventureSettingsPacket.setEntityId( this.player.getEntityId() );
        this.player.getConnection().send( adventureSettingsPacket );
    }

}
