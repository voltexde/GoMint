/*
 * Copyright (c) 2018 GoMint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.config.converter;

import io.gomint.GoMint;
import io.gomint.config.ConfigSection;
import io.gomint.math.Location;
import io.gomint.world.World;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
public class LocationConverter implements Converter {

    @Override
    public Object toConfig( Class<?> type, Object object, ParameterizedType parameterizedType ) throws Exception {
        Location location = (Location) object;
        Map<String, Object> saveMap = new HashMap<>();

        if ( location.getWorld() != null ) {
            saveMap.put( "world", location.getWorld().getWorldName() );
        }

        saveMap.put( "x", location.getX() );
        saveMap.put( "y", location.getY() );
        saveMap.put( "z", location.getZ() );
        saveMap.put( "yaw", location.getYaw() );
        saveMap.put( "pitch", location.getPitch() );

        return saveMap;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object fromConfig( Class type, Object object, ParameterizedType parameterizedType ) {
        World world = null;
        Float headYaw = null;
        Map<String, Object> locationMap;

        if ( object instanceof java.util.Map ) {
            locationMap = (Map<String, Object>) object;
        } else {
            locationMap = (Map<String, Object>) ( (ConfigSection) object ).getRawMap();
        }

        float x = this.getFloat( locationMap.get( "x" ) );
        float y = this.getFloat( locationMap.get( "y" ) );
        float z = this.getFloat( locationMap.get( "z" ) );
        float yaw = this.getFloat( locationMap.get( "yaw" ) );
        float pitch = this.getFloat( locationMap.get( "pitch" ) );

        if ( locationMap.containsKey( "world" ) ) {
            world = GoMint.instance().getWorld( (String) locationMap.get( "world" ) );
        }

        if ( locationMap.containsKey( "headYaw" ) ) {
            headYaw = (Float) locationMap.get( "headYaw" );
        }

        headYaw = headYaw == null ? yaw : headYaw;

        return new Location( world, x, y, z, headYaw, yaw, pitch );
    }

    @Override
    public boolean supports( Class<?> type ) {
        return Location.class.isAssignableFrom( type );
    }

    private float getFloat( Object object ) {
        if ( object instanceof Double ) {
            return ( (Double) object ).floatValue();
        } else if ( object instanceof Integer ) {
            return ( (Integer) object ).floatValue();
        } else if ( object instanceof Long ) {
            return ( (Long) object ).floatValue();
        }

        return (float) object;
    }

}
