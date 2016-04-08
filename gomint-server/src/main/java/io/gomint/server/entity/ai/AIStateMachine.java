package io.gomint.server.entity.ai;

/**
 * An AIStateMachine's task is to control the execution flow of different AI states
 * attached to it. It is responsible for updating the current AI state in order to
 * allow it to make changes to any entity it is associated with.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class AIStateMachine {

	private AIState activeState;

	/**
	 * Constructs a new AIStateMachine.
	 */
	public AIStateMachine() {

	}

	/**
	 * Gets the AI state that is currently active.
	 *
	 * @return The currently active AI state
	 */
	public AIState getActiveState() {
		return this.activeState;
	}

	/**
	 * Switches the currently active AI state. If next is set to null
	 * this function will behave exactly as {@link #stopExecution()}.
	 * In order to get the first AI state running the creator of the
	 * state machine will have to invoke this method handing in the
	 * initially active state.
	 *
	 * @param next The state to switch in next
	 */
	public void switchState( AIState next ) {
		if ( this.activeState != null ) {
			this.activeState.onLeave();
		}

		this.activeState = next;

		if ( this.activeState != null ) {
			this.activeState.onEnter();
		}
	}

	/**
	 * Stops the execution of any AI state that is currently active.
	 */
	public void stopExecution() {
		this.switchState( null );
	}

	/**
	 * Updates the state machine and the currently active AI state.
	 *
	 * @param currentTimeMS The current system time in milliseconds
	 * @param dT The time that has passed since the last update in seconds
	 */
	public void update( long currentTimeMS, float dT ) {
		if ( this.activeState != null ) {
			this.activeState.update( currentTimeMS, dT );
		}
	}

}
