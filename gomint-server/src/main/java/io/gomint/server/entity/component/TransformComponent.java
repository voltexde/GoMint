package io.gomint.server.entity.component;

import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.Transformable;
import io.gomint.server.world.WorldAdapter;

/**
 * A component that provides a solid implementation of Transformable and may be added to entities.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class TransformComponent implements EntityComponent, Transformable {

    private float posX;
    private float posY;
    private float posZ;

    private float yaw;
    private float headYaw;
    private float pitch;

    private boolean dirty;

    public TransformComponent() {

    }

    @Override
    public void update( long currentTimeMS, float dT ) {
        // Nothing to do here
    }

    @Override
    public float getPositionX() {
        return this.posX;
    }

    @Override
    public float getPositionY() {
        return this.posY;
    }

    @Override
    public float getPositionZ() {
        return this.posZ;
    }

    @Override
    public Vector getPosition() {
        return new Vector( this.posX, this.posY, this.posZ );
    }

    @Override
    public void setPosition( Vector position ) {
        this.posX = position.getX();
        this.posY = position.getY();
        this.posZ = position.getZ();
        this.dirty = true;
    }

    @Override
    public float getYaw() {
        return this.yaw;
    }

    @Override
    public void setYaw( float yaw ) {
        this.yaw = yaw;
        this.dirty = true;
    }

    @Override
    public float getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public void setHeadYaw( float headYaw ) {
        this.headYaw = headYaw;
        this.dirty = true;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void setPitch( float pitch ) {
        this.pitch = pitch;
        this.dirty = true;
    }

    @Override
    public Vector getDirection() {
        double r = Math.toRadians( this.yaw );
        double sinY = Math.sin( r );
        double cosY = Math.cos( r );
        double cosP = Math.cos( Math.toRadians( this.pitch ) );

        return new Vector( (float) ( cosY * cosP ), 0.0F, (float) ( sinY * cosP ) );
    }

    @Override
    public Vector getHeadDirection() {
        double rY = Math.toRadians( this.headYaw );
        double rP = Math.toRadians( this.pitch );
        double sinY = Math.sin( rY );
        double cosY = Math.cos( rY );
        double sinP = Math.sin( rP );
        double cosP = Math.cos( rP );

        return new Vector( (float) ( cosY * cosP ), (float) sinP, (float) ( sinY * cosP ) );
    }

    @Override
    public void setPosition( float positionX, float positionY, float positionZ ) {
        this.posX = positionX;
        this.posY = positionY;
        this.posZ = positionZ;
        this.dirty = true;
    }

    @Override
    public void move( float offsetX, float offsetY, float offsetZ ) {
        this.posX += offsetX;
        this.posY += offsetY;
        this.posZ += offsetZ;
        this.dirty = true;
    }

    @Override
    public void move( Vector offset ) {
        this.posX += offset.getX();
        this.posY += offset.getY();
        this.posZ += offset.getZ();
        this.dirty = true;
    }

    @Override
    public void rotateYaw( float yaw ) {
        // Add yaw rotation and normalize immediately:
        this.yaw += yaw;
        this.normalizeYaw();
        this.dirty = true;
    }

    @Override
    public void rotateHeadYaw( float headYaw ) {
        // Add head yaw rotation and normalize immediately:
        this.headYaw += headYaw;
        this.normalizeHeadYaw();
        this.dirty = true;
    }

    @Override
    public void rotatePitch( float pitch ) {
        // Add pitch rotation and normalize immediately:
        this.pitch += pitch;
        this.normalizePitch();
        this.dirty = true;
    }

    @Override
    public Location toLocation( WorldAdapter world ) {
        return new Location( world, this.posX, this.posY, this.posZ, this.yaw, this.pitch );
    }

    @Override
    public boolean isDirty() {
        boolean result = this.dirty;
        this.dirty = false;
        return result;
    }

    /**
     * Normalizes the yaw angle of the object's body.
     */
    private void normalizeYaw() {
        while ( this.yaw < -180.0F ) {
            this.yaw += 360.0F;
        }
        while ( this.yaw > +180.0F ) {
            this.yaw -= 360.0F;
        }
    }

    /**
     * Normalizes the yaw angle of the object's head.
     */
    private void normalizeHeadYaw() {
        while ( this.headYaw < -180.0F ) {
            this.headYaw += 360.0F;
        }
        while ( this.headYaw > +180.0F ) {
            this.headYaw -= 360.0F;
        }
    }

    /**
     * Normalizes the pitch angle of the object's head.
     */
    private void normalizePitch() {
        if ( this.pitch >= 90.0F ) {
            this.pitch = +89.9F;
        }
        if ( this.pitch <= -90.0F ) {
            this.pitch = -89.9F;
        }
    }
}