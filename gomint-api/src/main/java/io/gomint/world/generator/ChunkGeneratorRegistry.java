package io.gomint.world.generator;

import io.gomint.world.World;

import java.util.Collection;

public interface ChunkGeneratorRegistry {

    ChunkGenerator createGenerator(String name, World world, GeneratorContext ctx);

    boolean registerGenerator(String name, Class<? extends ChunkGenerator> generatorClass);

    boolean isGeneratorAvailable(String name);

    Collection<String> getRegisteredGenerators();

    Collection<Class<? extends ChunkGeneratorRegistry>> getRegisteredGeneratorClasses();

}
