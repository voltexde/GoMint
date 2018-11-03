/*
 * Copyright (c) 2018 GoMint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.config.converter;

import io.gomint.config.ConfigSection;
import io.gomint.math.BlockPosition;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockPositionConverter implements Converter {

    @Override
    public Object toConfig( Class<?> type, Object obj, ParameterizedType genericType ) {
        BlockPosition location = (BlockPosition) obj;
        Map<String, Object> saveMap = new HashMap<>();

        saveMap.put( "x", location.getX() );
        saveMap.put( "y", location.getY() );
        saveMap.put( "z", location.getZ() );

        return saveMap;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object fromConfig( Class type, Object section, ParameterizedType genericType ) {
        Map<String, Object> locationMap;
        if ( section instanceof java.util.Map ) {
            locationMap = (Map<String, Object>) section;
        } else {
            locationMap = (Map<String, Object>) ( (ConfigSection) section ).getRawMap();
        }
        return new BlockPosition(
            getInteger( locationMap.get( "x" ) ),
            getInteger( locationMap.get( "y" ) ),
            getInteger( locationMap.get( "z" ) ) );
    }

    @Override
    public boolean supports( Class<?> type ) {
        return BlockPosition.class.isAssignableFrom( type );
    }

    private int getInteger( Object obj ) {
        if ( obj instanceof Long ) {
            return ( (Long) obj ).intValue();
        }

        return (int) obj;
    }

}
