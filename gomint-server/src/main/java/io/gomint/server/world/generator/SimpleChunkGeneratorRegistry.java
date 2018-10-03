package io.gomint.server.world.generator;

import io.gomint.world.World;
import io.gomint.world.generator.ChunkGenerator;
import io.gomint.world.generator.ChunkGeneratorRegistry;
import io.gomint.world.generator.GeneratorContext;

public class SimpleChunkGeneratorRegistry implements ChunkGeneratorRegistry {

    @Override
    public ChunkGenerator createGenerator(String name, World world, GeneratorContext ctx) {
        return null;
    }

    @Override
    public boolean registerGenerator(String name, Class<? extends ChunkGenerator> generatorClass) {
        return false;
    }

    @Override
    public boolean isGeneratorAvailable(String name) {
        return false;
    }

    @Override
    public String[] getRegisteredGenerators() {
        return new String[0];
    }

    @Override
    public Class<? extends ChunkGenerator>[] getRegisteredGeneratorClasses() {
        return new Class[0];
    }

}
