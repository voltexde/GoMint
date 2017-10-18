package io.gomint.server.entity;

import io.gomint.server.entity.metadata.MetadataContainer;
import io.gomint.server.world.WorldAdapter;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EntityHuman extends EntityLiving {

    private static final int DATA_PLAYER_BED_POSITION = 29;

    /**
     * Constructs a new EntityLiving
     *
     * @param type  The type of the Entity
     * @param world The world in which this entity is in
     */
    protected EntityHuman( EntityType type, WorldAdapter world ) {
        super( type, world );
        this.metadataContainer.putByte( MetadataContainer.DATA_PLAYER_INDEX, (byte) 0 );

        // Sleeping stuff
        this.setPlayerFlag( EntityFlag.PLAYER_SLEEP, false );
        this.metadataContainer.putPosition( DATA_PLAYER_BED_POSITION, 0,0,0 );
    }

    /**
     * Set a player flag
     *
     * @param flag which should be set
     * @param value to what it should be set, true or false
     */
    public void setPlayerFlag( EntityFlag flag, boolean value ) {
        this.metadataContainer.setDataFlag( MetadataContainer.DATA_PLAYER_INDEX, flag, value );
    }

}
