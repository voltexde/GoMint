package io.gomint.server.world.generator;

import com.google.common.base.Preconditions;
import io.gomint.server.util.performance.ObjectConstructionFactory;
import io.gomint.world.World;
import io.gomint.world.generator.ChunkGenerator;
import io.gomint.world.generator.ChunkGeneratorRegistry;
import io.gomint.world.generator.GeneratorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Clockw1seLrd
 * @version 1.0
 */
public class SimpleChunkGeneratorRegistry implements ChunkGeneratorRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger( SimpleChunkGeneratorRegistry.class );

    private final Map<String, Class<? extends ChunkGenerator>> registeredGenerators = new HashMap<>();

    public SimpleChunkGeneratorRegistry() {
    }

    @Override
    public boolean registerGenerator( String name, Class<? extends ChunkGenerator> generatorClass ) {
        Preconditions.checkNotNull( generatorClass, "'generatorClass' cannot be null" );

        if ( this.isGeneratorAvailable( name ) ) {
            throw new IllegalStateException( "Chunk generator '" + name + "' is already registered" );
        }

        this.registeredGenerators.put( name, generatorClass );
        return true;
    }

    @Override
    public boolean isGeneratorAvailable( String name ) {
        return this.registeredGenerators.get( name ) != null;
    }

    @Override
    public Optional<Class<? extends ChunkGenerator>> getGeneratorClass( String name ) {
        return Optional.ofNullable( this.registeredGenerators.get( name ) );
    }

    @Override
    public Collection<String> getRegisteredGenerators() {
        return this.registeredGenerators.keySet();
    }

    @Override
    public Collection<Class<? extends ChunkGenerator>> getRegisteredGeneratorClasses() {
        return this.registeredGenerators.values();
    }

}
