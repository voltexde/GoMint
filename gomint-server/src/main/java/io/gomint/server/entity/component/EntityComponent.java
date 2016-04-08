package io.gomint.server.entity.component;

/**
 * Interface for component implementations. Components are used to add specific features to entities
 * such as health counters, AI, exhaustion and many more.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public interface EntityComponent {

	/**
	 * Updates the entity component.
	 *
	 * @param currentTimeMS The current system time in milliseconds
	 * @param dT The time that has passed since the last update in seconds
	 */
	void update( long currentTimeMS, float dT );

}
