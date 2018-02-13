/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import com.google.common.base.Preconditions;
import com.google.common.reflect.Reflection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author geNAZt
 * @version 1.0
 */
public final class ClassPath {

    private static Method findClass;
    private static final Logger LOGGER = LoggerFactory.getLogger( ClassPath.class );
    private static final Predicate<ClassInfo> IS_TOP_LEVEL = info -> info.className.indexOf( 36 ) == -1;
    private static final Set<ClassInfo> CLASSES = new HashSet<>();

    static {
        // Reflection for fast lookup classes
        try {
            ClassPath.findClass = ClassLoader.class.getDeclaredMethod( "findClass", String.class );
            ClassPath.findClass.setAccessible( true );
        } catch ( NoSuchMethodException e ) {
            ClassPath.findClass = null;
            LOGGER.warn( "Could not reflect fast findClass method: ", e );
        }

        // Scan the system classloader which is a url classloader for sure (the bootstrap checks that)
        Set<File> knownFiles = new HashSet<>();
        URL[] loadedURLs = ( (URLClassLoader) ClassLoader.getSystemClassLoader() ).getURLs();
        for ( URL url : loadedURLs ) {
            if ( url.getProtocol().equals( "file" ) ) {
                File file = toFile( url );
                if ( knownFiles.add( file ) ) {
                    if ( file.isFile() ) {
                        try ( JarFile jarFile = new JarFile( file ) ) {
                            Enumeration entries = jarFile.entries();

                            while ( entries.hasMoreElements() ) {
                                JarEntry entry = (JarEntry) entries.nextElement();
                                if ( !entry.isDirectory() && entry.getName().endsWith( ".class" ) ) {
                                    CLASSES.add( new ClassInfo( entry.getName() ) );
                                }
                            }
                        } catch ( IOException e ) {
                            LOGGER.warn( "IO Exception during JAR scanning: ", e );
                        }
                    } else {
                        try {
                            Set<File> currentPath = new HashSet<>();
                            currentPath.add( file.getCanonicalFile() );
                            scanDirectory( file, "", currentPath );
                        } catch ( IOException e ) {
                            LOGGER.warn( "Could not scan directory: ", e );
                        }
                    }
                }
            }
        }
    }

    private static File toFile( URL url ) {
        try {
            return new File( url.toURI() );
        } catch ( URISyntaxException var2 ) {
            return new File( url.getPath() );
        }
    }

    /**
     * Search for classes matching the given package name
     *
     * @param packageName for which we search all classes
     * @return all classes found in the given package
     */
    public static Set<ClassInfo> getTopLevelClasses( String packageName ) {
        Preconditions.checkNotNull( packageName );

        return CLASSES.stream()
            .filter( ClassInfo.class::isInstance )
            .map( ClassInfo.class::cast )
            .filter( IS_TOP_LEVEL )
            .filter( classInfo -> classInfo.getPackageName().equals( packageName ) )
            .collect( Collectors.toSet() );
    }

    private static void scanDirectory( File directory, String packagePrefix, Set<File> currentPath ) throws IOException {
        File[] files = directory.listFiles();
        if ( files == null ) {
            LOGGER.warn( "Cannot read directory {}", directory );
        } else {
            for ( File f : files ) {
                String name = f.getName();
                if ( f.isDirectory() ) {
                    File deref = f.getCanonicalFile();
                    if ( currentPath.add( deref ) ) {
                        scanDirectory( deref, packagePrefix + name + "/", currentPath );
                        currentPath.remove( deref );
                    }
                } else {
                    String resourceName = packagePrefix + name;
                    if ( resourceName.endsWith( ".class" ) ) {
                        CLASSES.add( new ClassInfo( resourceName ) );
                    }
                }
            }
        }
    }

    @EqualsAndHashCode( callSuper = false )
    @ToString
    public static final class ClassInfo {

        private final String className;

        private ClassInfo( String resourceName ) {
            // Extract class name
            int classNameEnd = resourceName.length() - ".class".length();
            this.className = resourceName.substring( 0, classNameEnd ).replace( '/', '.' );
        }

        /**
         * Get package of this class
         *
         * @return package of class
         */
        String getPackageName() {
            return Reflection.getPackageName( this.className );
        }

        /**
         * Load the class defined by this information holder
         *
         * @return loaded class
         */
        public Class<?> load() {
            try {
                return (Class<?>) ClassPath.findClass.invoke( ClassLoader.getSystemClassLoader(), this.className );
            } catch ( IllegalAccessException | InvocationTargetException e ) {
                // Just ignore for now
            }

            // Fallback to old slower loader
            try {
                return ClassLoader.getSystemClassLoader().loadClass( this.className );
            } catch ( ClassNotFoundException var2 ) {
                throw new IllegalStateException( var2 );
            }
        }
    }

}
