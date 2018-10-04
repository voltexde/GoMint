package io.gomint.world.generator;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Clockw1seLrd
 * @version 1.0
 */
public interface ChunkGeneratorRegistry {

    boolean registerGenerator( String name, Class<? extends ChunkGenerator> generatorClass );

    boolean isGeneratorAvailable( String name );

    Optional<Class<? extends ChunkGenerator>> getGeneratorClass( String name );

    Collection<String> getRegisteredGenerators();

    Collection<Class<? extends ChunkGenerator>> getRegisteredGeneratorClasses();

}
