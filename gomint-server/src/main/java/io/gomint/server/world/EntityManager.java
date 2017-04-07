/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.entity.Player;
import io.gomint.jraknet.PacketReliability;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.*;
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
            for ( Entity movedEntity : movedEntities ) {
                PacketEntityMovement packetEntityMovement = new PacketEntityMovement();
                packetEntityMovement.setEntityId( movedEntity.getEntityId() );

                packetEntityMovement.setX( movedEntity.getPositionX() );
                packetEntityMovement.setY( movedEntity.getPositionY() );
                packetEntityMovement.setZ( movedEntity.getPositionZ() );

                packetEntityMovement.setYaw( movedEntity.getYaw() );
                packetEntityMovement.setHeadYaw( movedEntity.getHeadYaw() );
                packetEntityMovement.setPitch( movedEntity.getPitch() );

                for ( EntityPlayer entityPlayer : this.world.getPlayers0().keySet() ) {
                    if ( movedEntity instanceof EntityPlayer ) {
                        if ( entityPlayer.isHidden( (Player) movedEntity ) || entityPlayer.equals( movedEntity )) {
                            continue;
                        }
                    }

                    entityPlayer.getConnection().addToSendQueue( packetEntityMovement );
                }
            }
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

        // Special case for players
        if ( entity instanceof EntityPlayer ) {
            EntityPlayer player = (EntityPlayer) entity;

            PacketSpawnPlayer packetSpawnPlayer = new PacketSpawnPlayer();
            packetSpawnPlayer.setUuid( player.getUUID() );
            packetSpawnPlayer.setName( player.getName() );
            packetSpawnPlayer.setEntityId( entity.getEntityId() );
            packetSpawnPlayer.setRuntimeEntityId( entity.getEntityId() );

            packetSpawnPlayer.setX( entity.getPositionX() );
            packetSpawnPlayer.setY( entity.getPositionY() );
            packetSpawnPlayer.setZ( entity.getPositionZ() );

            packetSpawnPlayer.setVelocityX( 0.0F );
            packetSpawnPlayer.setVelocityY( 0.0F );
            packetSpawnPlayer.setVelocityZ( 0.0F );

            packetSpawnPlayer.setPitch( entity.getPitch() );
            packetSpawnPlayer.setYaw( entity.getYaw() );
            packetSpawnPlayer.setHeadYaw( entity.getHeadYaw() );

            packetSpawnPlayer.setItemInHand( player.getInventory().getItemInHand() );
            packetSpawnPlayer.setMetadataContainer( player.getMetadata() );

            for ( EntityPlayer entityPlayer : this.world.getPlayers0().keySet() ) {
                if ( !entityPlayer.isHidden( player ) && !entityPlayer.equals( player ) ) {
                    entityPlayer.getConnection().send( packetSpawnPlayer );
                }
            }
        } else {
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