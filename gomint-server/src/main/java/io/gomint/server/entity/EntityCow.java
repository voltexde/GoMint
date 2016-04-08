package io.gomint.server.entity;

import io.gomint.server.world.WorldAdapter;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class EntityCow extends Entity {
	
	/**
	 * Construct a new cow
	 *
	 * @param world The world in which this entity is in
	 */
	public EntityCow( WorldAdapter world ) {
		super( EntityType.COW, world );
	}

}
