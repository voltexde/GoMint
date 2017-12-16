package io.gomint.entity.passive;

import io.gomint.GoMint;
import io.gomint.entity.EntityLiving;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface EntityVillager extends EntityLiving {

    /**
     * Create a new entity villager with no config
     *
     * @return empty, fresh villager
     */
    static EntityVillager create() {
        return GoMint.instance().createEntity( EntityVillager.class );
    }

}
