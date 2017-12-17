package io.gomint.server.entity.monster;

import io.gomint.server.entity.Attribute;
import io.gomint.server.entity.EntityLiving;
import io.gomint.server.entity.EntityType;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.WorldAdapter;

@RegisterInfo( id = 37 )
public class EntitySlime extends EntityLiving implements io.gomint.entity.monster.EntitySlime {

    /**
     * Constructs a new EntityLiving
     *
     * @param world The world in which this entity is in
     */
    public EntitySlime( WorldAdapter world ) {
        super( EntityType.SLIME, world );
        this.initEntity();
    }

    /**
     * Create new entity slime for API
     */
    public EntitySlime() {
        super( EntityType.SLIME, null );
        this.initEntity();
    }

    private void initEntity() {
        this.setSize( 2.04f, 2.04f );
        this.addAttribute( Attribute.HEALTH );
        this.setMaxHealth( 16 );
        this.setHealth( 16 );
    }

    @Override
    public void update( long currentTimeMS, float dT ) {
        super.update( currentTimeMS, dT );
    }
}
