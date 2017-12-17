package io.gomint.entity.monster;

import io.gomint.GoMint;
import io.gomint.entity.EntityLiving;

public interface EntityMagmaCube extends EntityLiving {

    /**
     * Create a new entity bat with no config
     *
     * @return empty, fresh magma cube
     */
    static EntityMagmaCube create() {
        return GoMint.instance().createEntity( EntityMagmaCube.class );
    }
}
