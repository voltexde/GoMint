/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.converter.anvil.entity.v1_8;

import io.gomint.math.MathUtils;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.inventory.item.Items;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @param <T> type of entity
 * @author geNAZt
 * @version 1.0
 */
public abstract class EntityConverter<T extends Entity> extends BasisConverter<T> {

    public EntityConverter( Items items, Object2IntMap<String> itemConverter ) {
        super( items, itemConverter );
    }

    @Override
    public T readFrom( NBTTagCompound compound ) {
        // Create empty entity
        T entity = this.createEntity();

        // Set position
        Vector position = getPosition( compound );
        entity.setPosition( position );

        // Set motion
        Vector motion = getMotion( compound );
        entity.getTransform().setMotion( motion.getX(), motion.getY(), motion.getZ() );

        // Set rotation
        List<Object> rotation = compound.getList( "Rotation", false );
        float yaw = MathUtils.ensureFloat( rotation.get( 0 ) );
        float pitch = MathUtils.ensureFloat( rotation.get( 1 ) );
        entity.setYaw( yaw );
        entity.setPitch( pitch );

        // Set age
        if ( compound.containsKey( "Age" ) ) {
            entity.setAge( compound.getInteger( "Age", 0 ) * 50L, TimeUnit.MILLISECONDS );
        }

        return entity;
    }

}
