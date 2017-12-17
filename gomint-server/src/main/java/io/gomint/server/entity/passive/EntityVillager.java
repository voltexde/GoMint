package io.gomint.server.entity.passive;

import io.gomint.server.entity.Attribute;
import io.gomint.server.entity.EntityLiving;
import io.gomint.server.entity.EntityType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.WorldAdapter;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 15 )
public class EntityVillager extends EntityLiving implements io.gomint.entity.passive.EntityVillager {

    /**
     * Constructs a new EntityLiving
     *
     * @param world The world in which this entity is in
     */
    public EntityVillager( WorldAdapter world ) {
        super( EntityType.VILLAGER, world );
        this.initEntity();
    }

    /**
     * Create new entity villager for API
     */
    public EntityVillager() {
        super( EntityType.VILLAGER, null );
        this.initEntity();
    }

    private void initEntity() {
        this.setSize( 0.6f, 1.95f );
        this.eyeHeight = 1.62f;
        this.addAttribute( Attribute.HEALTH );
        this.setMaxHealth( 20 );
        this.setHealth( 20 );
    }

    @Override
    public void update( long currentTimeMS, float dT ) {
        super.update( currentTimeMS, dT );
    }

}
