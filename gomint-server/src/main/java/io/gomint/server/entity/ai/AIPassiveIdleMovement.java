package io.gomint.server.entity.ai;

import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.pathfinding.PathfindingEngine;
import io.gomint.server.util.IntTriple;
import io.gomint.server.world.WorldAdapter;

import java.util.List;
import java.util.Random;

/**
 * AI state that implements the passive movement of idling animals.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class AIPassiveIdleMovement extends AIState {

	private final WorldAdapter world;
	private final PathfindingEngine pathfinding;

	private int currentPathNode;
	private List<IntTriple> path;

	/**
	 * Constructs a new AIPassiveIdleMovement that will belong to the given state machine.
	 *
	 * @param machine The state machine the AIState being constructed belongs to
	 */
	public AIPassiveIdleMovement( AIStateMachine machine, WorldAdapter world, PathfindingEngine pathfinding ) {
		super( machine );
		this.world = world;
		this.pathfinding = pathfinding;
	}

	@Override
	public void update( long currentTimeMS, float dT ) {
		if ( this.path != null && this.currentPathNode < this.path.size() ) {
			Vector position = this.pathfinding.getTransform().getPosition();

			IntTriple blockPosition = new IntTriple( (int) position.getX(), (int) position.getY(), (int) position.getZ() );
			IntTriple node          = this.path.get( this.currentPathNode );

			Vector direction = node.toVector().subtract( position ).normalize().multiply( 0.05F * dT );
			this.pathfinding.getTransform().setPosition( position.add( direction ) );

			if ( blockPosition.equals( node ) ) {
				this.currentPathNode++;
			}
		} else {
			this.pathfinding.setGoal( this.generateRandomGoal() );
			this.path = this.pathfinding.getPath();
			this.currentPathNode = 0;
		}
	}

	/**
	 * Generates a new random goal within a reasonable distance from the object's
	 * current position.
	 *
	 * @return The generated goal
	 */
	private Location generateRandomGoal() {
		// Generates a new random goal inside a 5 block circle around the entity:
		final double MAX_DISTANCE = 5.0D;
		final Random random = this.world.getRandom();

		double t = 2 * Math.PI * random.nextDouble();
		double r = MAX_DISTANCE * random.nextDouble();
		double x = r * Math.cos( t );
		double z = r * Math.sin( t );

		Vector position = this.pathfinding.getTransform().getPosition().clone();
		position.add( (float) x, 0.0F, (float) z );

		return new Location( this.world, position );
	}

}
