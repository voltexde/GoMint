package io.gomint.event.player;

import io.gomint.entity.EntityPlayer;

public class PlayerSpinEvent extends CancellablePlayerEvent {

    private final boolean newStatus;

    public PlayerSpinEvent(EntityPlayer player, boolean newStatus) {
        super(player);
        this.newStatus = newStatus;
    }

    /**
     * Get the status the client wants to set.
     *
     * @return true when the client wants to start spinning, false otherwise
     */
    public boolean getNewStatus() {
        return this.newStatus;
    }
}
