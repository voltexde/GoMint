/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util.performance;

import java.lang.reflect.Constructor;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ObjectConstructionFactory implements ConstructionFactory {

    private static boolean USE_SUN;

    static {
        try {
            Class test = Class.forName( "sun.reflect.ReflectionFactory" );
            test.getDeclaredMethod( "newConstructorAccessor", Constructor.class );
            USE_SUN = true;
        } catch ( Exception e ) {
            USE_SUN = false;
        }
    }

    private final ConstructionFactory factory;

    public ObjectConstructionFactory( Class<?> clazz ) {
        if ( USE_SUN ) {
            this.factory = new ConstructorAccessFactory( clazz );
        } else {
            this.factory = new ReflectionAccessFactory( clazz );
        }
    }

    @Override
    public Object newInstance() {
        return this.factory.newInstance();
    }

}
