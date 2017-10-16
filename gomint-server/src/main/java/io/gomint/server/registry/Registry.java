package io.gomint.server.registry;

import com.google.common.reflect.ClassPath;
import io.gomint.server.util.collection.GeneratorAPIClassMap;
import io.gomint.server.util.collection.GeneratorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Registry<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger( Registry.class );

    private final GeneratorCallback<R> generatorCallback;
    private final GeneratorMap<R> generators = GeneratorMap.withExpectedSize( 250 );
    private final GeneratorAPIClassMap<Class<?>> apiReferences = GeneratorAPIClassMap.withExpectedSize( 250 );

    /**
     * Build a new generator registry
     *
     * @param callback which is used to generate a generator for each found element
     */
    public Registry( GeneratorCallback<R> callback ) {
        this.generatorCallback = callback;
    }

    /**
     * Register all classes which can be found in given path
     *
     * @param classPath which should be searched
     */
    public void register( String classPath ) {
        LOGGER.debug( "Going to scan: " + classPath );

        // Search io.gomint.server.inventory.item
        try {
            for ( ClassPath.ClassInfo classInfo : ClassPath.from( ClassLoader.getSystemClassLoader() )
                .getTopLevelClasses( classPath ) ) {
                register( classInfo.load() );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void register( Class<?> clazz ) {
        // We need register info
        if ( !clazz.isAnnotationPresent( RegisterInfo.class ) ) {
            LOGGER.debug( "No register info annotation present" );
            return;
        }

        int id = clazz.getAnnotation( RegisterInfo.class ).id();
        R oldGen = this.generators.get( id );
        if ( oldGen != null ) {
            LOGGER.debug( "Duplicated register info for id: " + id + " -> " + clazz.getName() + "; old: " + oldGen.getClass().getName() );
        }

        R generator = this.generatorCallback.generate( id, clazz );
        if ( generator != null ) {
            this.generators.put( id, generator );

            // Check for API interfaces
            for ( Class<?> apiInter : clazz.getInterfaces() ) {
                apiReferences.put( apiInter, id );
            }

            apiReferences.put( clazz, id );
        }
    }

    public R getGenerator( Class<?> clazz ) {
        // Get the internal ID
        int id = apiReferences.getOrDefault( clazz, -1 );
        if ( id == -1 ) {
            return null;
        }

        return getGenerator( id );
    }

    public R getGenerator( int id ) {
        return generators.get( id );
    }

    public Collection<Integer> getAll() {
        return generators.keySet();
    }

}
