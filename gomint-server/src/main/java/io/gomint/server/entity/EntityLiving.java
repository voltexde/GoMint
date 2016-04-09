package io.gomint.server.entity;

import io.gomint.server.entity.component.AIBehaviourComponent;
import io.gomint.server.entity.pathfinding.PathfindingEngine;
import io.gomint.server.world.WorldAdapter;

/**
 * Common base class for all entities that live. All living entities possess
 * an AI which is the significant characteristic that marks an entity as being
 * alive in GoMint's definition.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class EntityLiving extends Entity {

	// AI of the entity:
	protected AIBehaviourComponent behaviour;
	// Pathfinding engine of the entity:
	protected PathfindingEngine pathfinding;

	/**
	 * Constructs a new EntityLiving
	 *
	 * @param type  The type of the Entity
	 * @param world The world in which this entity is in
	 */
	protected EntityLiving( EntityType type, WorldAdapter world ) {
		super( type, world );
		this.behaviour = new AIBehaviourComponent();
		this.pathfinding = new PathfindingEngine( this.getTransform() );
	}

	// ==================================== UPDATING ==================================== //

	@Override
	public void update( long currentTimeMS, float dT ) {
		super.update( currentTimeMS, dT );
		this.behaviour.update( currentTimeMS, dT );
	}

}
