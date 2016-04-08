/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.component.TransformComponent;
import io.gomint.server.entity.metadata.MetadataContainer;
import io.gomint.server.world.WorldAdapter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Base class for all entities. Defines accessors to attributes and components that are
 * common to all entities such as ID, type and transformation.
 *
 * @author BlackyPaw
 * @version 1.1
 */
public abstract class Entity {

	private static final AtomicLong ENTITY_ID = new AtomicLong( 0 );

	/**
	 * The id of this entity
	 */
	protected final long          id;
	/**
	 * Type of the entity
	 */
	protected final EntityType   type;

	protected WorldAdapter world;
	private TransformComponent transform;

	/**
	 * Construct a new Entity
	 *
	 * @param type The type of the Entity
	 * @param world The world in which this entity is in
	 */
	protected Entity( EntityType type, WorldAdapter world ) {
		this.id = ENTITY_ID.incrementAndGet();
		this.type = type;
		this.world = world;
		this.transform = new TransformComponent();
	}

	// ==================================== ACCESSORS ==================================== //

	/**
	 * Gets this entity's unique ID.
	 *
	 * @return The entitiy's unique ID
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Gets the type of this entity.
	 *
	 * @return The type of this entity
	 */
	public EntityType getType() {
		return this.type;
	}

	/**
	 * Gets the world of this entity.
	 *
	 * @return The world of this entity
	 */
	public WorldAdapter getWorld() {
		return this.world;
	}

	/**
	 * Gets a metadata container containing all metadata values of this entity.
	 *
	 * @return This entity's metadata
	 */
	public MetadataContainer getMetadata() {
		return new MetadataContainer();
	}

	/**
	 * Despawns this entity if it is currently spawned into any world.
	 */
	public void despawn() {
		this.world.despawnEntity( this.id );
	}

	// ==================================== UPDATING ==================================== //

	/**
	 * Updates the entity and all components attached to it.
	 *
	 * @param currentTimeMS The current system time in milliseconds
	 * @param dT The time that has passed since the last tick in 1/s
	 */
	public void update( long currentTimeMS, float dT ) {
		this.transform.update( currentTimeMS, dT );
	}

	// ==================================== TRANSFORMATION ==================================== //

	/**
	 * Gets the entity's transform as a Transformable.
	 *
	 * @return The entity's transform
	 */
	Transformable getTransform() {
		return this.transform;
	}

	/**
	 * Gets the location of this entity.
	 *
	 * @return The location of this entity
	 */
	public Location getLocation() {
		return this.transform.toLocation( this.world );
	}

	/**
	 * Gets the position of the entity on the x axis.
	 *
	 * @return The position of the entity on the x axis
	 */
	public float getPositionX() {
		return this.transform.getPositionX();
	}

	/**
	 * Gets the position of the entity on the y axis.
	 *
	 * @return The position of the entity on the y axis
	 */
	public float getPositionY() {
		return this.transform.getPositionY();
	}

	/**
	 * Gets the position of the entity on the z axis.
	 *
	 * @return The position of the entity on the z axis
	 */
	public float getPositionZ() {
		return this.transform.getPositionZ();
	}

	/**
	 * Gets the position of the entity as a vector.
	 *
	 * @return The position of the entity as a vector
	 */
	public Vector getPosition() {
		return this.transform.getPosition();
	}

	/**
	 * Gets the yaw angle of the entity's body.
	 *
	 * @return The yaw angle of the entity's body
	 */
	public float getYaw() {
		return this.transform.getYaw();
	}

	/**
	 * Gets the yaw angle of the entity's head.
	 *
	 * @return The yaw angle of the entity's head
	 */
	public float getHeadYaw() {
		return this.transform.getHeadYaw();
	}

	/**
	 * Gets the pitch angle of the entity's head.
	 *
	 * @return The pitch angle of the entity's head
	 */
	public float getPitch() {
		return this.transform.getPitch();
	}

	/**
	 * Gets the direction the entity's body is facing as a normalized vector.
	 * Note, though, that pitch rotation is considered to be part of the entity's
	 * head and is thus not included inside the vector returned by this function.
	 *
	 * @return The direction vector the entity's body is facing
	 */
	public Vector getDirection() {
		return this.transform.getDirection();
	}

	/**
	 * Gets the direction the entity's head is facing as a normalized vector.
	 *
	 * @return The direction vector the entity's head is facing
	 */
	public Vector getHeadDirection() {
		return this.transform.getHeadDirection();
	}

	/**
	 * Sets the entity's position given the respective coordinates on the 3 axes.
	 *
	 * @param positionX The x coordinate of the position
	 * @param positionY The y coordinate of the position
	 * @param positionZ The z coordinate of the position
	 */
	public void setPosition( float positionX, float positionY, float positionZ ) {
		this.transform.setPosition( positionX, positionY, positionZ );
	}

	/**
	 * Sets the entity's position given a vector.
	 *
	 * @param position The position to set
	 */
	public void setPosition( Vector position ) {
		this.transform.setPosition( position );
	}

	/**
	 * Sets the yaw angle of the entity's body.
	 *
	 * @param yaw The yaw angle to set
	 */
	public void setYaw( float yaw ) {
		this.transform.setYaw( yaw );
	}

	/**
	 * Sets the yaw angle of the entity's head.
	 *
	 * @param headYaw The yaw angle to set
	 */
	public void setHeadYaw( float headYaw ) {
		this.transform.setHeadYaw( headYaw );
	}

	/**
	 * Sets the pitch angle of the entity's head.
	 *
	 * @param pitch The pitch angle to set.
	 */
	public void setPitch( float pitch ) {
		this.transform.setPitch( pitch );
	}

	/**
	 * Moves the entity by the given offset vector. Produces the same result as
	 * <pre>
	 * {@code
	 * Entity.setPosition( Entity.getPosition().add( offsetX, offsetY, offsetZ ) );
	 * }
	 * </pre>
	 *
	 * @param offsetX The x component of the offset
	 * @param offsetY The y component of the offset
	 * @param offsetZ The z component of the offset
	 */
	public void move( float offsetX, float offsetY, float offsetZ ) {
		this.transform.move( offsetX, offsetY, offsetZ );
	}

	/**
	 * Moves the entity by the given offset vector. Produces the same result as
	 * <pre>
	 * {@code
	 * Entity.setPosition( Entity.getPosition().add( offsetX, offsetY, offsetZ ) );
	 * }
	 * </pre>
	 *
	 * @param offset The offset vector to apply to the entity
	 */
	public void move( Vector offset ) {
		this.transform.move( offset );
	}

	/**
	 * Rotates the entity's body around the yaw axis (vertical axis).
	 *
	 * @param yaw The yaw value by which to rotate the entity
	 */
	public void rotateYaw( float yaw ) {
		this.transform.rotateYaw( yaw );
	}

	/**
	 * Rotates the entity's head around the yaw axis (vertical axis).
	 *
	 * @param headYaw The yaw value by which to rotate the entity's head
	 */
	public void rotateHeadYaw( float headYaw ) {
		this.transform.rotateHeadYaw( headYaw );
	}

	/**
	 * Rotates the entity's head around the pitch axis (transverse axis).
	 *
	 * @param pitch The pitch value by which to rotate the entity's head
	 */
	public void rotatePitch( float pitch ) {
		this.transform.rotatePitch( pitch );
	}

}