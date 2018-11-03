/*
 * Copyright (c) 2018 GoMint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.config.converter;

import io.gomint.config.BaseConfigMapper;
import io.gomint.config.InternalConverter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ListConverter implements Converter {

    private InternalConverter internalConverter;

    public ListConverter( InternalConverter internalConverter ) {
        this.internalConverter = internalConverter;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object toConfig( Class<?> type, Object object, ParameterizedType parameterizedType ) throws Exception {
        List values = (List) object;
        List converted = new ArrayList();

        if ( this.internalConverter.getConfig() instanceof BaseConfigMapper ) {
            BaseConfigMapper baseConfigMapper = (BaseConfigMapper) this.internalConverter.getConfig();
            baseConfigMapper.addCommentPrefix( "-" );
        }

        for ( Object value : values ) {
            Converter converter = this.internalConverter.getConverter( value.getClass() );

            if ( converter != null ) {
                converted.add( converter.toConfig( value.getClass(), value, null ) );
            } else {
                converted.add( value );
            }
        }

        if ( this.internalConverter.getConfig() instanceof BaseConfigMapper ) {
            BaseConfigMapper baseConfigMapper = (BaseConfigMapper) this.internalConverter.getConfig();
            baseConfigMapper.removeCommentPrefix( "-" );
        }

        return converted;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object fromConfig( Class type, Object object, ParameterizedType parameterizedType ) throws Exception {
        List converted = new ArrayList();

        try {
            converted = ( (List) type.newInstance() );
        } catch ( Exception ignored ) {

        }

        List values = (List) object;

        if ( parameterizedType != null && parameterizedType.getActualTypeArguments()[0] instanceof Class ) {
            Converter converter = this.internalConverter.getConverter( (Class) parameterizedType.getActualTypeArguments()[0] );

            if ( converter != null ) {
                for ( Object value : values ) {
                    converted.add( converter.fromConfig( (Class) parameterizedType.getActualTypeArguments()[0], value, null ) );
                }
            } else {
                converted = values;
            }
        } else {
            converted = values;
        }

        return converted;
    }

    @Override
    public boolean supports( Class<?> type ) {
        return List.class.isAssignableFrom( type );
    }

}
