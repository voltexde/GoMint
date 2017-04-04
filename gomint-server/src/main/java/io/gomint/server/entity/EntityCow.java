package io.gomint.server.entity;

import io.gomint.server.entity.ai.AIPassiveIdleMovement;
import io.gomint.server.world.WorldAdapter;

/**
 * The Entity implementation for cows. Registers AI behaviour and manages other components
 * specific for cows.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class EntityCow extends EntityLiving {

    /**
     * Constructs a new cow entity which will have an AI.
     *
     * @param world The world in which this entity is in
     */
    public EntityCow( WorldAdapter world ) {
        super( EntityType.COW, world );
        this.setupAI();
    }

    /**
     * Sets up the AI of the entity by creating the respective AI states, linking them
     * to each other and finally adding them to the AI state machine of the entity's
     * AI behaviour component.
     */
    private void setupAI() {
        AIPassiveIdleMovement idle = new AIPassiveIdleMovement( this.behaviour.getMachine(), this.world, this.pathfinding );
        this.behaviour.getMachine().switchState( idle );
    }

}
