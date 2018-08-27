package io.gomint.server.scoreboard;

import io.gomint.server.entity.Entity;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketRemoveObjective;
import io.gomint.server.network.packet.PacketSetObjective;
import io.gomint.server.network.packet.PacketSetScore;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Scoreboard {

    // Scores
    private long scoreIdCounter = 0;
    private Long2ObjectMap<ScoreboardLine> scoreboardLines = new Long2ObjectArrayMap<>();

    private Long2LongMap scoreIdToEntityId = new Long2LongOpenHashMap();
    private Long2ObjectMap<String> scoreIdToFakeEntity = new Long2ObjectOpenHashMap<>();

    // Viewers
    private Set<EntityPlayer> viewers = new HashSet<>();

    // Displays
    private Map<DisplaySlot, ScoreboardDisplay> displays = new EnumMap<>( DisplaySlot.class );

    /**
     * Add a new display slot to this scoreboard
     *
     * @param slot          which should be shown
     * @param objectiveName which should be used
     * @param displayName   of the slot
     */
    public ScoreboardDisplay addDisplaySlot( DisplaySlot slot, String objectiveName, String displayName ) {
        ScoreboardDisplay scoreboardDisplay = this.displays.get( slot );
        if ( scoreboardDisplay == null ) {
            scoreboardDisplay = new ScoreboardDisplay( this, objectiveName, displayName );
            this.displays.put( slot, scoreboardDisplay );

            this.broadcast( this.constructDisplayPacket( slot, scoreboardDisplay ) );
        }

        return scoreboardDisplay;
    }

    private Packet constructDisplayPacket( DisplaySlot slot, ScoreboardDisplay display ) {
        PacketSetObjective packetSetObjective = new PacketSetObjective();
        packetSetObjective.setCriteriaName( "dummy" );
        packetSetObjective.setDisplayName( display.getDisplayName() );
        packetSetObjective.setObjectiveName( display.getObjectiveName() );
        packetSetObjective.setDisplaySlot( slot.name().toLowerCase() );
        return packetSetObjective;
    }

    private void broadcast( Packet packet ) {
        for ( EntityPlayer viewer : this.viewers ) {
            viewer.getConnection().addToSendQueue( packet );
        }
    }

    void addString( String name, String objective, int score ) {
        // Check if we already have this registered
        Long2ObjectMap.FastEntrySet<ScoreboardLine> fastEntrySet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
        ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastEntrySet.fastIterator();
        while ( fastIterator.hasNext() ) {
            Long2ObjectMap.Entry<ScoreboardLine> entry = fastIterator.next();
            if ( entry.getValue().type == 3 && entry.getValue().fakeName.equals( name ) && entry.getValue().objective.equals( objective ) ) {
                return;
            }
        }

        // Add this score
        long newId = this.scoreIdCounter++;
        ScoreboardLine line = new ScoreboardLine( (byte) 3, 0, name, objective, score );
        this.scoreboardLines.put( newId, line );

        // Broadcast entry
        this.broadcast( this.constructSetScore() );
    }

    /**
     * Add a entity to the given scoreboard
     *
     * @param entity    which should be added
     * @param objective which should be used
     * @param score     which should be used to register
     */
    void addEntity( Entity entity, String objective, int score ) {
        // Check if we already have this registered
        Long2ObjectMap.FastEntrySet<ScoreboardLine> fastEntrySet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
        ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastEntrySet.fastIterator();
        while ( fastIterator.hasNext() ) {
            Long2ObjectMap.Entry<ScoreboardLine> entry = fastIterator.next();
            if ( entry.getValue().entityId == entity.getEntityId() && entry.getValue().objective.equals( objective ) ) {
                return;
            }
        }

        // Add this score
        long newId = this.scoreIdCounter++;
        ScoreboardLine line = new ScoreboardLine( (byte) ( ( entity instanceof EntityPlayer ) ? 1 : 2 ), entity.getEntityId(), "", objective, score );
        this.scoreboardLines.put( newId, line );

        // Broadcast entry
        this.broadcast( this.constructSetScore() );
    }

    private Packet constructSetScore() {
        PacketSetScore packetSetScore = new PacketSetScore();
        packetSetScore.setType( (byte) 0 );

        List<PacketSetScore.ScoreEntry> entries = new ArrayList<>();
        Long2ObjectMap.FastEntrySet<ScoreboardLine> fastEntrySet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
        ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastEntrySet.fastIterator();
        while ( fastIterator.hasNext() ) {
            Long2ObjectMap.Entry<ScoreboardLine> entry = fastIterator.next();
            entries.add( new PacketSetScore.ScoreEntry( entry.getLongKey(), entry.getValue().objective, entry.getValue().score, entry.getValue().type, entry.getValue().fakeName, entry.getValue().entityId ) );
        }

        packetSetScore.setEntries( entries );
        return packetSetScore;
    }

    public void showFor( EntityPlayer player ) {
        if ( this.viewers.add( player ) ) {
            // Remove player from old scoreboard
            if ( player.getScoreboard() != null ) {
                player.getScoreboard().hideFor( player );
            }

            // Set new scoreboard
            player.setScoreboard( this );
            showForPlayer( player );
        }
    }

    public void hideFor( EntityPlayer player ) {
        if ( this.viewers.remove( player ) ) {
            // Remove all known scores
            LongList validScoreIDs = new LongArrayList();

            // Map fake entries first
            Long2ObjectMap.FastEntrySet<String> fastSet = (Long2ObjectMap.FastEntrySet<String>) this.scoreIdToFakeEntity.long2ObjectEntrySet();
            ObjectIterator<Long2ObjectMap.Entry<String>> fastIterator = fastSet.fastIterator();
            while ( fastIterator.hasNext() ) {
                validScoreIDs.add( fastIterator.next().getLongKey() );
            }

            // Map entities now
            Long2LongMap.FastEntrySet l2lFastSet = (Long2LongMap.FastEntrySet) this.scoreIdToEntityId.long2LongEntrySet();
            ObjectIterator<Long2LongMap.Entry> l2lFastIterator = l2lFastSet.fastIterator();
            while ( l2lFastIterator.hasNext() ) {
                validScoreIDs.add( l2lFastIterator.next().getLongKey() );
            }

            // Remove all scores
            player.getConnection().addToSendQueue( this.constructRemoveScores( validScoreIDs ) );

            // Remove all known displays
            for ( Map.Entry<DisplaySlot, ScoreboardDisplay> entry : this.displays.entrySet() ) {
                player.getConnection().addToSendQueue( this.constructRemoveDisplayPacket( entry.getValue() ) );
            }
        }
    }

    private Packet constructRemoveScores( LongList scoreIDs ) {
        PacketSetScore packetSetScore = new PacketSetScore();
        packetSetScore.setType( (byte) 1 );

        List<PacketSetScore.ScoreEntry> entries = new ArrayList<>();
        for ( long scoreID : scoreIDs ) {
            entries.add( new PacketSetScore.ScoreEntry( scoreID, "", 0 ) );
        }

        packetSetScore.setEntries( entries );
        return packetSetScore;
    }

    private Packet constructRemoveDisplayPacket( ScoreboardDisplay display ) {
        PacketRemoveObjective packetRemoveObjective = new PacketRemoveObjective();
        packetRemoveObjective.setObjectiveName( display.getObjectiveName() );
        return packetRemoveObjective;
    }

    private void showForPlayer( EntityPlayer player ) {
        // We send display information first
        for ( Map.Entry<DisplaySlot, ScoreboardDisplay> entry : this.displays.entrySet() ) {
            player.getConnection().addToSendQueue( this.constructDisplayPacket( entry.getKey(), entry.getValue() ) );
        }

        // Send scores
        player.getConnection().addToSendQueue( this.constructSetScore() );
    }

    @AllArgsConstructor
    @Data
    private class ScoreboardLine {
        private final byte type;
        private final long entityId;
        private final String fakeName;
        private final String objective;

        private int score;
    }

}
