/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.entity.Entity;
import io.gomint.server.entity.generator.EntityGenerator;
import io.gomint.server.registry.GeneratorCallback;
import io.gomint.server.registry.Registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Entities {

    private static final Registry<EntityGenerator> GENERATORS = new Registry<>( new GeneratorCallback<EntityGenerator>() {
        @Override
        public EntityGenerator generate( Class<?> clazz ) {
            try {
                return (EntityGenerator) Entities.class.getClassLoader().loadClass( "io.gomint.server.entity.generator." + clazz.getSimpleName() + "Generator" ).newInstance();
            } catch ( InstantiationException | IllegalAccessException | ClassNotFoundException e ) {
                e.printStackTrace();
            }

            return null;
        }
    } );

    static {
        GENERATORS.register( "io.gomint.server.entity" );
        GENERATORS.register( "io.gomint.server.entity.active" );
        GENERATORS.register( "io.gomint.server.entity.monster" );
        GENERATORS.register( "io.gomint.server.entity.passive" );
        GENERATORS.register( "io.gomint.server.entity.projectile" );
    }

    public static <T extends Entity> T create( Class<T> entityClass ) {
        EntityGenerator entityGenerator = GENERATORS.getGenerator( entityClass );
        if ( entityGenerator == null ) {
            return null;
        }

        return entityGenerator.generate();
    }

}
