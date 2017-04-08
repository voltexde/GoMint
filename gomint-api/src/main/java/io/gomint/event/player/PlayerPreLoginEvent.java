package io.gomint.event.player;

import io.gomint.event.CancellableEvent;

import java.net.InetSocketAddress;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerPreLoginEvent extends CancellableEvent {

    private final InetSocketAddress clientAddress;

    /**
     * Construct a new player pre login event. This is fired before the login payload is going to be verified.
     *
     * @param clientAddress The address of the client which wants to login
     */
    public PlayerPreLoginEvent( InetSocketAddress clientAddress ) {
        this.clientAddress = clientAddress;
    }

    /**
     * Get the clients internet address
     *
     * @return the internet address of the client
     */
    public InetSocketAddress getClientAddress() {
        return this.clientAddress;
    }

}
