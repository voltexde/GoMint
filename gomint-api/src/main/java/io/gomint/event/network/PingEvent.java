package io.gomint.event.network;

import io.gomint.event.Event;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PingEvent extends Event {

    private String motd;
    private int onlinePlayers;
    private int maxPlayers;

    public PingEvent( String motd, int onlinePlayers, int maxPlayers ) {
        this.motd = motd;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getMotd() {
        return this.motd;
    }

    public void setMotd( String motd ) {
        this.motd = motd;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

    public void setOnlinePlayers( int onlinePlayers ) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers( int maxPlayers ) {
        this.maxPlayers = maxPlayers;
    }

}
