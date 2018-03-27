/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.potion;

import io.gomint.server.entity.potion.effect.Effect;
import io.gomint.server.entity.potion.generator.EffectGenerator;
import io.gomint.server.registry.GeneratorCallback;
import io.gomint.server.registry.Registry;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Effects {

    private static final Registry<EffectGenerator> GENERATORS = new Registry<>( new GeneratorCallback<EffectGenerator>() {
        @Override
        public EffectGenerator generate( Class<?> clazz ) {
            try {
                // Use the same code source as the Gomint JAR
                return (EffectGenerator) Effects.class.getClassLoader().loadClass( "io.gomint.server.entity.potion.generator." + clazz.getSimpleName() + "Generator" ).newInstance();
            } catch ( InstantiationException | IllegalAccessException | ClassNotFoundException e ) {
                e.printStackTrace();
            }

            return null;
        }
    } );

    static {
        GENERATORS.register( "io.gomint.server.entity.potion.effect" );
    }

    public static Effect generate( int id, int amplifier, long lengthInMS ) {
        EffectGenerator instance = GENERATORS.getGenerator( id );
        if ( instance != null ) {
            return instance.generate( amplifier, lengthInMS );
        }

        return null;
    }

}
