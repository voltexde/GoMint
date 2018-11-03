/*
 * Copyright (c) 2018 GoMint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.config;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * @author geNAZt
 * @version 1.0
 */
public class YamlConfig extends ConfigMapper implements IConfig {

    public YamlConfig() {

    }

    public YamlConfig( String filename ) {
        this.configFile = new File( filename + ( filename.endsWith( ".yml" ) ? "" : ".yml" ) );
    }

    @Override
    public void save() throws InvalidConfigurationException {
        if ( configFile == null ) {
            throw new IllegalArgumentException( "Saving a config without given File" );
        }

        if ( this.root == null ) {
            this.root = new ConfigSection();
        }

        this.clearComments();
        this.internalSave( getClass() );
        this.saveToYaml();
    }

    @Override
    public void save( File file ) throws InvalidConfigurationException {
        if ( file == null ) {
            throw new IllegalArgumentException( "File argument can not be null" );
        }

        this.configFile = file;
        this.save();
    }

    @Override
    public void init() throws InvalidConfigurationException {
        if ( !this.configFile.exists() ) {
            if ( this.configFile.getParentFile() != null ) {
                if ( !this.configFile.getParentFile().mkdirs() ) {
                    throw new RuntimeException( "Failed creating directory " + this.configFile.getParentFile().getAbsolutePath() );
                }
            }

            try {
                if ( !this.configFile.createNewFile() ) {
                    throw new RuntimeException( "Failed creating file " + this.configFile.getAbsolutePath() );
                }

                this.save();
            } catch ( IOException cause ) {
                throw new InvalidConfigurationException( "Could not create new empty Config", cause );
            }
        } else {
            this.load();
        }
    }

    @Override
    public void init( File file ) throws InvalidConfigurationException {
        Preconditions.checkNotNull( file, "File argument can not be null" );

        this.configFile = file;
        this.init();
    }

    @Override
    public void reload() throws InvalidConfigurationException {
        this.loadFromYaml();
        this.internalLoad( this.getClass() );
    }

    @Override
    public void load() throws InvalidConfigurationException {
        Preconditions.checkNotNull( this.configFile, "Loading a config without given File" );

        this.loadFromYaml();
        this.update( this.root );
        this.internalLoad( this.getClass() );
    }

    @Override
    public void load( File file ) throws InvalidConfigurationException {
        Preconditions.checkNotNull( file, "File argument can not be null" );

        this.configFile = file;
        this.load();
    }

    private void internalSave( Class clazz ) throws InvalidConfigurationException {
        if ( !clazz.getSuperclass().equals( YamlConfig.class ) ) {
            this.internalSave( clazz.getSuperclass() );
        }

        for ( Field field : clazz.getDeclaredFields() ) {
            if ( this.doSkip( field ) ) {
                continue;
            }

            String path;

            switch ( this.configMode ) {
                case PATH_BY_UNDERSCORE:
                    path = field.getName().replace( "_", "." );
                    break;
                case FIELD_IS_KEY:
                    path = field.getName();
                    break;
                case DEFAULT:
                default:
                    String fieldName = field.getName();

                    if ( fieldName.contains( "_" ) ) {
                        path = field.getName().replace( "_", "." );
                    } else {
                        path = field.getName();
                    }

                    break;
            }

            ArrayList<String> comments = new ArrayList<>();

            for ( Annotation annotation : field.getAnnotations() ) {
                if ( annotation instanceof Comment ) {
                    comments.add( ( (Comment) annotation ).value() );
                }

                if ( annotation instanceof Comments ) {
                    for ( Comment comment : ( (Comments) annotation ).value() ) {
                        comments.add( comment.value() + "\n" );
                    }
                }
            }

            if ( field.isAnnotationPresent( Path.class ) ) {
                path = field.getAnnotation( Path.class ).value();
            }

            if ( comments.size() > 0 ) {
                for ( String comment : comments ) {
                    this.addComment( path, comment );
                }
            }

            if ( Modifier.isPrivate( field.getModifiers() ) ) {
                field.setAccessible( true );
            }

            try {
                this.converter.toConfig( this, field, this.root, path );
                this.converter.fromConfig( this, field, this.root, path );
            } catch ( Exception cause ) {
                if ( !this.skipFailedObjects ) {
                    throw new InvalidConfigurationException( "Could not save the Field", cause );
                }
            }
        }
    }

    private void internalLoad( Class clazz ) throws InvalidConfigurationException {
        if ( !clazz.getSuperclass().equals( YamlConfig.class ) ) {
            this.internalLoad( clazz.getSuperclass() );
        }

        boolean save = false;

        for ( Field field : clazz.getDeclaredFields() ) {
            if ( this.doSkip( field ) ) {
                continue;
            }

            String path = configMode.equals( ConfigMode.PATH_BY_UNDERSCORE ) ? field.getName().replaceAll( "_", "." ) : field.getName();

            if ( field.isAnnotationPresent( Path.class ) ) {
                path = field.getAnnotation( Path.class ).value();
            }

            if ( Modifier.isPrivate( field.getModifiers() ) ) {
                field.setAccessible( true );
            }

            if ( this.root.has( path ) ) {
                try {
                    converter.fromConfig( this, field, this.root, path );
                } catch ( Exception e ) {
                    throw new InvalidConfigurationException( "Could not set field", e );
                }
            } else {
                try {
                    this.converter.toConfig( this, field, this.root, path );
                    this.converter.fromConfig( this, field, this.root, path );

                    save = true;
                } catch ( Exception cause ) {
                    if ( !skipFailedObjects ) {
                        throw new InvalidConfigurationException( "Could not get field", cause );
                    }
                }
            }
        }

        if ( save ) {
            this.saveToYaml();
        }
    }

}
