package io.gomint.server.entity.component;

import io.gomint.server.entity.ai.AIStateMachine;

/**
 * A component that adds an AIStateMachine to an entity which will be able to
 * modify the entity's state.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class AIBehaviourComponent implements EntityComponent {

	private AIStateMachine machine;

	/**
	 * Constructs a new AIBehaviourComponent.
	 */
	public AIBehaviourComponent() {
		this.machine = new AIStateMachine();
	}

	/**
	 * Gets the AI state machine attached to this component.
	 * It may be used to build up a state hierarchy and to start
	 * the execution of the AI.
	 *
	 * @return The AI state machine attached to this component.
	 */
	public AIStateMachine getMachine() {
		return this.machine;
	}

	@Override
	public void update( long currentTimeMS, float dT ) {
		this.machine.update( currentTimeMS, dT );
	}

}
