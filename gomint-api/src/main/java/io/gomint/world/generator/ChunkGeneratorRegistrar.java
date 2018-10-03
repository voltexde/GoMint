package io.gomint.world.generator;

import io.gomint.world.World;

public interface ChunkGeneratorRegistrar {

    ChunkGenerator createGenerator(String name, World world, GeneratorContext ctx);

    boolean registerGenerator(String name, Class<? extends ChunkGenerator> generatorClass);

    boolean isGeneratorAvailable(String name);

    String[] getRegisteredGenerators();

    Class<? extends ChunkGenerator>[] getRegisteredGeneratorClasses();

}
