package io.gomint.server.entity.passive;

import io.gomint.server.entity.Attribute;
import io.gomint.server.entity.EntityLiving;
import io.gomint.server.entity.EntityType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.WorldAdapter;

@RegisterInfo( id = 28 )
public class EntityPolarBear extends EntityLiving implements io.gomint.entity.passive.EntityPolarBear {

    /**
     * Constructs a new EntityLiving
     *
     * @param type  The type of the Entity
     * @param world The world in which this entity is in
     */
    protected EntityPolarBear( EntityType type, WorldAdapter world ) {
        super( type, world );
        this.initEntity();
    }

    /**
     * Create new entity polarbear for API
     */
    public EntityPolarBear() {
        super( EntityType.POLAR_BEAR, null );
        this.initEntity();
    }

    private void initEntity() {
        this.setSize( 1.3f, 1.4f );
        this.addAttribute( Attribute.HEALTH );
    }

    @Override
    public void update( long currentTimeMS, float dT ) {
        super.update( currentTimeMS, dT );
    }
}
