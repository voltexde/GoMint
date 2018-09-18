/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

<<<<<<< HEAD
=======
import io.gomint.server.inventory.item.Items;
>>>>>>> origin/WIP
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class MobSpawnerTileEntity extends TileEntity {

    private int entityId;

    // Display
    private float displayWidth;
    private float displayScale;
    private float displayHeight;

    // Spawn rules
    private short maxNearbyEntities;
    private short playerRange;
    private short spawnRange;

    // Delay rules
    private short maxDelay;
    private short minDelay;
    private short delay;
    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
	 */
    public MobSpawnerTileEntity( NBTTagCompound tagCompound, WorldAdapter world, Items items ) {
        super( tagCompound, world, items );

        this.entityId = tagCompound.getInteger( "EntityId", 0 );

        this.displayWidth = tagCompound.getFloat( "DisplayEntityWidth", 0.8f );
        this.displayScale = tagCompound.getFloat( "DisplayEntityScale", 1.0f );
        this.displayHeight = tagCompound.getFloat( "DisplayEntityHeight", 1.8f );

        this.maxNearbyEntities = tagCompound.getShort( "MaxNearbyEntities", (short) 6 );
        this.playerRange = tagCompound.getShort( "RequiredPlayerRange", (short) 16 );
        this.spawnRange = tagCompound.getShort( "SpawnRange", (short) 4 );

        this.maxDelay = tagCompound.getShort( "MaxSpawnDelay", (short) 800 );
        this.minDelay = tagCompound.getShort( "MinSpawnDelay", (short) 200 );
        this.delay = tagCompound.getShort( "Delay", (short) 0 );
    }

    @Override
    public void update( long currentMillis ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound, SerializationReason reason ) {
        super.toCompound( compound, reason );

        compound.addValue( "id", "MobSpawner" );

        if ( reason == SerializationReason.PERSIST ) {
            compound.addValue( "MaxNearbyEntities", this.maxNearbyEntities );
            compound.addValue( "RequiredPlayerRange", this.playerRange );
            compound.addValue( "SpawnRange", this.spawnRange );

            compound.addValue( "MaxSpawnDelay", this.maxDelay );
            compound.addValue( "MinSpawnDelay", this.minDelay );
            compound.addValue( "Delay", this.delay );
        }

        compound.addValue( "EntityId", this.entityId );
        compound.addValue( "DisplayEntityWidth", this.displayWidth );
        compound.addValue( "DisplayEntityScale", this.displayScale );
        compound.addValue( "DisplayEntityHeight", this.displayHeight );
    }

}
