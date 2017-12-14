/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.entity;

import io.gomint.math.AxisAlignedBB;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.math.Vector2;
import io.gomint.world.World;

import java.util.concurrent.TimeUnit;

/**
 * @author BlackyPaw
 * @author geNAZt
 * @version 1.0
 */
public interface Entity {

    /**
     * Gets the entity's unqiue identifier.
     *
     * @return The entity's unique identifier
     */
    long getEntityId();

    /**
     * Gets the world the entity resides in.
     *
     * @return The world the entity resides in
     */
    World getWorld();

    /**
     * Gets the location of the entity.
     *
     * @return The entity's location
     */
    Location getLocation();

    /**
     * Get current x axis position
     *
     * @return position on x axis
     */
    float getPositionX();

    /**
     * Get current y axis position
     *
     * @return position on y axis
     */
    float getPositionY();

    /**
     * Get current z axis position
     *
     * @return position on z axis
     */
    float getPositionZ();

    /**
     * Get current pitch
     *
     * @return current pitch
     */
    float getPitch();

    /**
     * Get current yaw
     *
     * @return current yaw
     */
    float getYaw();

    /**
     * Set a entities velocity
     *
     * @param velocity to set
     */
    void setVelocity( Vector velocity );

    /**
     * Get current applied velocity
     *
     * @return applied velocity
     */
    Vector getVelocity();

    /**
     * Get the name tag of this entity
     *
     * The name tag is shown above the entity in the client
     *
     * @return The name tag of the entity
     */
    String getNameTag();

    /**
     * Set the name tag of this entity
     *
     * The name tag is shown above the entity in the client
     *
     * @param nameTag The new name tag of this entity
     */
    void setNameTag( String nameTag );

    /**
     * Set name tag to always visible even when not looking at it
     *
     * @param value true for always visible, otherwise false
     */
    void setNameTagAlwaysVisible( boolean value );

    boolean isNameTagAlwaysVisible();

    void setNameTagVisible( boolean value );

    boolean isNameTagVisible();

    AxisAlignedBB getBoundingBox();

    boolean isOnGround();

    /**
     * Get the dead status of this entity
     *
     * @return true when dead, false when alive
     */
    boolean isDead();

    /**
     * Spawn this entity
     *
     * @param location where the entity should spawn
     */
    void spawn( Location location );

    /**
     * Teleport to the given location
     *
     * @param to The location where the entity should be teleported to
     */
    void teleport( Location to );

    /**
     * Despawn this entity on the next tick
     */
    void despawn();

    /**
     * Get a vector in which direction the entity is looking
     *
     * @return vector which shows in which direction the entity is looking
     */
    Vector2 getDirectionVector();

    /**
     * Set the age of this entity. This can be used to control automatic despawning.
     *
     * @param duration which will be multiplied with the given unit
     * @param unit of time
     */
    void setAge( long duration, TimeUnit unit );

    /**
     * Disable ticking of this entity. This causes the given entity to stop moving, it also stops decaying,
     * aging and all the other stuff which requires ticking.
     *
     * @param value true when the entity should tick, false when not
     */
    void setTicking( boolean value );

    /**
     * Check if this entity is currently allow to tick.
     *
     * @return true when ticking, false when not
     */
    boolean isTicking();

    /**
     * Create if needed and return the entities boss bar
     *
     * @return boss bar of this entity
     */
    BossBar getBossBar();

}
