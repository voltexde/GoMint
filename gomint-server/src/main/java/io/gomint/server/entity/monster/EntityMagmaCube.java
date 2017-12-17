package io.gomint.server.entity.monster;

import io.gomint.server.entity.Attribute;
import io.gomint.server.entity.EntityLiving;
import io.gomint.server.entity.EntityType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.WorldAdapter;

@RegisterInfo( id = 42 )
public class EntityMagmaCube extends EntityLiving implements io.gomint.entity.monster.EntityMagmaCube {

    /**
     * Constructs a new EntityLiving
     *
     * @param world The world in which this entity is in
     */
    public EntityMagmaCube( WorldAdapter world ) {
        super( EntityType.MAGMA_CUBE, world );
        this.initEntity();
    }

    /**
     * Create new entity magma cube for API
     */
    public EntityMagmaCube() {
        super( EntityType.MAGMA_CUBE, null );
        this.initEntity();
    }

    private void initEntity() {
        this.setSize( 2.04f, 2.04f );
        this.addAttribute( Attribute.HEALTH );
    }

    @Override
    public void update( long currentTimeMS, float dT ) {
        super.update( currentTimeMS, dT );
    }
}
