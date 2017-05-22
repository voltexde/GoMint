package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.Location;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketStartGame extends Packet {
    private long entityId;
    private long runtimeEntityId;
    private Location spawn;
    private int seed;
    private int dimension;
    private int generator;
    private int gamemode;
    private int difficulty;
    private int x;
    private int y;
    private int z;
    private boolean hasAchievementsDisabled = true;
    private int dayCycleStopTime;
    private boolean eduMode;
    private float rainLevel;
    private float lightningLevel;
    private boolean commandsEnabled;
    private boolean isTexturePacksRequired;
    private String secret;
    private String worldName;

    public PacketStartGame() {
        super( Protocol.PACKET_START_GAME );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeSignedVarLong( this.entityId );
        buffer.writeUnsignedVarLong( this.runtimeEntityId );
        buffer.writeLFloat( (float) this.spawn.getX() );
        buffer.writeLFloat( (float) this.spawn.getY() );
        buffer.writeLFloat( (float) this.spawn.getZ() );
        buffer.writeLFloat( this.spawn.getYaw() );
        buffer.writeLFloat( this.spawn.getPitch() );
        buffer.writeSignedVarInt( this.seed );
        buffer.writeSignedVarInt( this.dimension );
        buffer.writeSignedVarInt( this.generator );
        buffer.writeSignedVarInt( this.gamemode );
        buffer.writeSignedVarInt( this.difficulty );
        buffer.writeSignedVarInt( (int) this.spawn.getX() );
        buffer.writeSignedVarInt( (int) this.spawn.getY() );
        buffer.writeSignedVarInt( (int) this.spawn.getZ() );
        buffer.writeBoolean( this.hasAchievementsDisabled );
        buffer.writeSignedVarInt( this.dayCycleStopTime );
        buffer.writeBoolean( this.eduMode );
        buffer.writeLFloat( this.rainLevel );
        buffer.writeLFloat( this.lightningLevel );
        buffer.writeBoolean( this.commandsEnabled );
        buffer.writeBoolean( this.isTexturePacksRequired );
        buffer.writeString( this.secret );
        buffer.writeString( this.worldName );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {

    }
}
