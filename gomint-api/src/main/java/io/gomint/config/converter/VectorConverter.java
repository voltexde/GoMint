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
    public Object toConfig( Class<?> type, Object obj, ParameterizedType genericType ) throws Exception {
        Vector vector = (Vector) obj;
        Map<String, Object> saveMap = new HashMap<>();

        saveMap.put( "x", vector.getX() );
        saveMap.put( "y", vector.getY() );
        saveMap.put( "z", vector.getZ() );

        return saveMap;
    }

    @Override
    public Object fromConfig( Class type, Object section, ParameterizedType genericType ) throws Exception {
        Map<String, Object> vectorMap;
        if ( section instanceof java.util.Map ) {
            vectorMap = (Map<String, Object>) section;
        } else {
            vectorMap = (Map<String, Object>) ( (ConfigSection) section ).getRawMap();
        }

        return new Vector( getFloat( vectorMap.get( "x" ) ), getFloat( vectorMap.get( "y" ) ), getFloat( vectorMap.get( "z" ) ) );
    }

    @Override
    public boolean supports( Class<?> type ) {
        return Vector.class.isAssignableFrom( type );
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

}
