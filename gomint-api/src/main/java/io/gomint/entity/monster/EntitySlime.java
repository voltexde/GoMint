package io.gomint.entity.monster;

import io.gomint.GoMint;
import io.gomint.entity.EntityLiving;

public interface EntitySlime extends EntityLiving {

    /**
     * Create a new entity bat with no config
     *
     * @return empty, fresh slime
     */
    static EntitySlime create() {
        return GoMint.instance().createEntity( EntitySlime.class );
    }
}
