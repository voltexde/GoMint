/*
 * Copyright (c) 2018 GoMint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.config.converter;

import io.gomint.config.ConfigSection;
import io.gomint.math.Vector;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
public class VectorConverter extends BaseConverter {

    @Override
    public Object toConfig( Class<?> type, Object object, ParameterizedType parameterizedType ) {
        Vector vector = (Vector) object;
        Map<String, Object> saveMap = new HashMap<>();

        saveMap.put( "x", vector.getX() );
        saveMap.put( "y", vector.getY() );
        saveMap.put( "z", vector.getZ() );

        return saveMap;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object fromConfig( Class type, Object object, ParameterizedType parameterizedType ) {
        Map<String, Object> vectorMap;

        if ( object instanceof Map ) {
            vectorMap = (Map<String, Object>) object;
        } else {
            vectorMap = (Map<String, Object>) ( (ConfigSection) object ).getRawMap();
        }

        return new Vector(
            super.asFloat( vectorMap.get( "x" ) ),
            super.asFloat( vectorMap.get( "y" ) ),
            super.asFloat( vectorMap.get( "z" ) )
        );
    }

    @Override
    public boolean supports( Class<?> type ) {
        return Vector.class.isAssignableFrom( type );
    }

}
