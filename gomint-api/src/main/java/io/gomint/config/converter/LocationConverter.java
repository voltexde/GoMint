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
    public Object toConfig( Class<?> type, Object obj, ParameterizedType genericType ) throws Exception {
        Location location = (Location) obj;
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
    public Object fromConfig( Class type, Object section, ParameterizedType genericType ) throws Exception {
        Map<String, Object> locationMap;
        if ( section instanceof java.util.Map ) {
            locationMap = (Map<String, Object>) section;
        } else {
            locationMap = (Map<String, Object>) ( (ConfigSection) section ).getRawMap();
        }

        float yaw = getFloat( locationMap.get( "yaw" ) );
        float pitch = getFloat( locationMap.get( "pitch" ) );

        World world = null;
        if ( locationMap.containsKey( "world" ) ) {
            world = GoMint.instance().getWorld( (String) locationMap.get( "world" ) );
        }

        Float headYaw = null;
        if ( locationMap.containsKey( "headYaw" ) ) {
            headYaw = (Float) locationMap.get( "headYaw" );
        }

        return new Location( world,
            getFloat( locationMap.get( "x" ) ),
            getFloat( locationMap.get( "y" ) ),
            getFloat( locationMap.get( "z" ) ),
            headYaw == null ? yaw : headYaw,
            yaw,
            pitch );
    }

    private float getFloat( Object obj ) {
        if ( obj instanceof Double ) {
            return ( (Double) obj ).floatValue();
        } else if ( obj instanceof Integer ) {
            return ( (Integer) obj ).floatValue();
        } else if ( obj instanceof Long ) {
            return ( (Long) obj ).floatValue();
        }

        return (float) obj;
    }

    @Override
    public boolean supports( Class<?> type ) {
        return Location.class.isAssignableFrom( type );
    }

}
