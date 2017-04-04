/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.jraknet.PacketReliability;
import io.gomint.server.entity.Entity;
import io.gomint.server.network.packet.PacketDespawnEntity;
import io.gomint.server.network.packet.PacketEntityMotion;
import io.gomint.server.network.packet.PacketEntityMovement;
import io.gomint.server.network.packet.PacketSpawnEntity;
import net.openhft.koloboke.collect.map.LongObjCursor;
import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class that manages all entities inside a world.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class EntityManager {

    private final WorldAdapter world;
    private LongObjMap<Entity> entitiesById;

    public EntityManager( WorldAdapter world ) {
        this.world = world;
        this.entitiesById = HashLongObjMaps.newMutableMap();
    }

    /**
     * Updates all entities managed by the EntityManager.
     *
     * @param currentTimeMS The current system time in milliseconds
     * @param dT            The time that has passed since the last update in seconds
     */
    public void update( long currentTimeMS, float dT ) {
        // --------------------------------------
        // Update all entities:
        Set<Entity> movedEntities = null;
        LongObjCursor<Entity> cursor = this.entitiesById.cursor();
        while ( cursor.moveNext() ) {
            Entity entity = cursor.value();
            entity.update( currentTimeMS, dT );
            if ( entity.getTransform().isDirty() ) {
                if ( movedEntities == null ) {
                    movedEntities = new HashSet<>();
                }

                movedEntities.add( entity );
            }
        }

        // --------------------------------------
        // Create movement batches:
        if ( movedEntities != null && movedEntities.size() > 0 ) {
            PacketEntityMovement movement = new PacketEntityMovement();
            PacketEntityMotion motion = new PacketEntityMotion();

            long[] entityId = new long[movedEntities.size()];
            float[] x = new float[movedEntities.size()];
            float[] y = new float[movedEntities.size()];
            float[] z = new float[movedEntities.size()];
            float[] yaw = new float[movedEntities.size()];
            float[] headYaw = new float[movedEntities.size()];
            float[] pitch = new float[movedEntities.size()];

            float[] velocityX = new float[movedEntities.size()];
            float[] velocityY = new float[movedEntities.size()];
            float[] velocityZ = new float[movedEntities.size()];

            int position = 0;
            for ( Entity entity : movedEntities ) {
                entityId[position] = entity.getEntityId();
                x[position] = entity.getPositionX();
                y[position] = entity.getPositionY();
                z[position] = entity.getPositionZ();
                yaw[position] = entity.getYaw();
                headYaw[position] = entity.getHeadYaw();
                pitch[position] = entity.getPitch();

                velocityX[position] = 0.0F;
                velocityY[position] = 0.0F;
                velocityZ[position] = 0.0F;

                position++;
            }

            movement.setEntityId( entityId );
            movement.setX( x );
            movement.setY( y );
            movement.setZ( z );
            movement.setYaw( yaw );
            movement.setHeadYaw( headYaw );
            movement.setPitch( pitch );

            motion.setEntityId( entityId );
            motion.setVelocityX( velocityX );
            motion.setVelocityY( velocityY );
            motion.setVelocityZ( velocityZ );

            this.world.broadcast( PacketReliability.RELIABLE_SEQUENCED, 0, movement );
            this.world.broadcast( PacketReliability.RELIABLE_SEQUENCED, 0, motion );
        }
    }

    /**
     * Gets an entity given its unique ID.
     *
     * @param entityId The entity's unique ID
     * @return The entity if found or null otherwise
     */
    public Entity findEntity( long entityId ) {
        return this.entitiesById.get( entityId );
    }

    /**
     * Spawns the given entity at the specified position.
     *
     * @param entity    The entity to spawn
     * @param positionX The x coordinate to spawn the entity at
     * @param positionY The y coordinate to spawn the entity at
     * @param positionZ The z coordinate to spawn the entity at
     */
    public void spawnEntityAt( Entity entity, float positionX, float positionY, float positionZ ) {
        this.spawnEntityAt( entity, positionX, positionY, positionZ, 0.0F, 0.0F );
    }

    /**
     * Spawns the given entity at the specified position with the specified rotation.
     *
     * @param entity    The entity to spawn
     * @param positionX The x coordinate to spawn the entity at
     * @param positionY The y coordinate to spawn the entity at
     * @param positionZ The z coordinate to spawn the entity at
     * @param yaw       The yaw value of the entity ; will be applied to both the entity's body and head
     * @param pitch     The pitch value of the entity
     */
    public void spawnEntityAt( Entity entity, float positionX, float positionY, float positionZ, float yaw, float pitch ) {
        entity.setPosition( positionX, positionY, positionZ );
        entity.setYaw( yaw );
        entity.setHeadYaw( yaw );
        entity.setPitch( pitch );
        this.entitiesById.put( entity.getEntityId(), entity );

        // Broadcast spawn entity packet:
        PacketSpawnEntity packet = new PacketSpawnEntity();
        packet.setEntityId( entity.getEntityId() );
        packet.setEntityType( entity.getType() );
        packet.setX( positionX );
        packet.setY( positionY );
        packet.setZ( positionZ );
        packet.setVelocityX( 0.0F );
        packet.setVelocityY( 0.0F );
        packet.setVelocityZ( 0.0F );
        packet.setYaw( yaw );
        packet.setHeadYaw( yaw );
        packet.setMetadata( entity.getMetadata() );
        this.world.broadcast( PacketReliability.RELIABLE, 0, packet );
    }

    /**
     * Despawns an entity given its unique ID.
     *
     * @param entityId The unique ID of the entity
     */
    public void despawnEntity( long entityId ) {
        Entity entity = this.entitiesById.remove( entityId );
        if ( entity != null ) {
            // Broadcast despawn entity packet:
            PacketDespawnEntity packet = new PacketDespawnEntity();
            packet.setEntityId( entityId );
            this.world.broadcast( PacketReliability.RELIABLE, 0, packet );
        }
    }

}