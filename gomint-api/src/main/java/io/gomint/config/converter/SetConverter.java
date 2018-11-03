/*
 * Copyright (c) 2018 GoMint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.config.converter;

import io.gomint.config.InternalConverter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SetConverter implements Converter {

    private InternalConverter internalConverter;

    public SetConverter( InternalConverter internalConverter ) {
        this.internalConverter = internalConverter;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object toConfig( Class<?> type, Object obj, ParameterizedType genericType ) throws Exception {
        Set<Object> values = (Set<Object>) obj;
        List newList = new ArrayList();

        Iterator<Object> iterator = values.iterator();
        while ( iterator.hasNext() ) {
            Object val = iterator.next();

            Converter converter = internalConverter.getConverter( val.getClass() );

            if ( converter != null ) {
                newList.add( converter.toConfig( val.getClass(), val, null ) );
            } else {
                newList.add( val );
            }
        }

        return newList;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object fromConfig( Class type, Object section, ParameterizedType genericType ) throws Exception {
        List<Object> values = (List<Object>) section;
        Set<Object> newList = new HashSet<>();

        try {
            newList = (Set<Object>) type.newInstance();
        } catch ( Exception e ) {
        }

        if ( genericType != null && genericType.getActualTypeArguments()[0] instanceof Class ) {
            Converter converter = internalConverter.getConverter( (Class) genericType.getActualTypeArguments()[0] );

            if ( converter != null ) {
                for ( int i = 0; i < values.size(); i++ ) {
                    newList.add( converter.fromConfig( (Class) genericType.getActualTypeArguments()[0], values.get( i ), null ) );
                }
            } else {
                newList.addAll( values );
            }
        } else {
            newList.addAll( values );
        }

        return newList;
    }

    @Override
    public boolean supports( Class<?> type ) {
        return Set.class.isAssignableFrom( type );
    }

}
