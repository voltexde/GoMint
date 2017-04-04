package io.gomint.server.entity.ai;

/**
 * An enumeration of supported types for AI events.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public enum AIEventType {

    /**
     * The entity took damage for some reason.
     */
    DAMAGE,

    /**
     * The entity was attacked and took damage due to this.
     */
    DAMAGE_BY_ATTACK;

}
