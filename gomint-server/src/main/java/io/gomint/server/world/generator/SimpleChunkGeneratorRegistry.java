package io.gomint.server.world.generator;

import io.gomint.world.World;
import io.gomint.world.generator.ChunkGenerator;
import io.gomint.world.generator.ChunkGeneratorRegistry;
import io.gomint.world.generator.GeneratorContext;

import java.util.Collection;

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
    public Collection<String> getRegisteredGenerators() {
        return this.registeredGenerators.keySet();
    }

    @Override
    public Collection<Class<? extends ChunkGeneratorRegistry>> getRegisteredGeneratorClasses() {
        return this.registeredGenerators.values();
    }

}
