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
public class VectorConverter implements Converter {

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

        if ( object instanceof java.util.Map ) {
            vectorMap = (Map<String, Object>) object;
        } else {
            vectorMap = (Map<String, Object>) ( (ConfigSection) object ).getRawMap();
        }

        return new Vector(
            this.getFloat( vectorMap.get( "x" ) ),
            this.getFloat( vectorMap.get( "y" ) ),
            this.getFloat( vectorMap.get( "z" ) )
        );
    }

    @Override
    public boolean supports( Class<?> type ) {
        return Vector.class.isAssignableFrom( type );
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
