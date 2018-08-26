package io.gomint.server.scoreboard;

import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketRemoveObjective;
import io.gomint.server.network.packet.PacketSetObjective;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.*;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Scoreboard {

    // Scores
    private long scoreIdCounter = 0;
    private Long2LongMap scoreIdToEntityId = new Long2LongOpenHashMap();
    private Long2ObjectMap<String> scoreIdToFakeEntity = new Long2ObjectOpenHashMap<>();

    // Viewers
    private Set<EntityPlayer> viewers = new HashSet<>();

    // Displays
    private Set<DisplaySlot> shownDisplays = EnumSet.noneOf( DisplaySlot.class );
    private Map<DisplaySlot, String> displayNames = new EnumMap<>( DisplaySlot.class );

    /**
     * Add a new display slot to this scoreboard
     *
     * @param slot        which should be shown
     * @param displayName of the slot
     */
    public void addDisplaySlot( DisplaySlot slot, String displayName ) {
        if ( this.shownDisplays.add( slot ) ) {
            this.displayNames.put( slot, displayName );

            this.broadcast( this.constructDisplayPacket( slot, displayName ) );
        }
    }

    private Packet constructDisplayPacket( DisplaySlot slot, String displayName ) {
        PacketSetObjective packetSetObjective = new PacketSetObjective();
        packetSetObjective.setCriteriaName( "dummy" );
        packetSetObjective.setDisplayName( displayName );
        packetSetObjective.setObjectiveName( slot.name() + "_1" );
        packetSetObjective.setDisplaySlot( slot.name().toLowerCase() );
        return packetSetObjective;
    }

    private void broadcast( Packet packet ) {
        for ( EntityPlayer viewer : this.viewers ) {
            viewer.getConnection().addToSendQueue( packet );
        }
    }

    /**
     * Add a player to the given scoreboard
     *
     * @param player which should be added
     * @param score  which should be used to register
     */
    public void addPlayer( EntityPlayer player, int score ) {

    }

    private void removePlayer( EntityPlayer player ) {

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

            // Remove all known displays
            for ( DisplaySlot display : this.shownDisplays ) {
                player.getConnection().addToSendQueue( this.constructRemoveDisplayPacket( display ) );
            }
        }
    }

    private Packet constructRemoveDisplayPacket( DisplaySlot display ) {
        PacketRemoveObjective packetRemoveObjective = new PacketRemoveObjective();
        packetRemoveObjective.setObjectiveName( display.name() + "_1" );
        return packetRemoveObjective;
    }

    private void showForPlayer( EntityPlayer player ) {
        // We send display information first
        for ( DisplaySlot display : this.shownDisplays ) {
            player.getConnection().addToSendQueue( this.constructDisplayPacket( display, this.displayNames.get( display ) ) );
        }
    }



}
