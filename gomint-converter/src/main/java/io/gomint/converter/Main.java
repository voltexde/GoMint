/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.converter;

import io.gomint.server.assets.AssetsLibrary;
import io.gomint.server.inventory.item.Items;
import io.gomint.server.util.ClassPath;
import io.gomint.server.world.converter.anvil.AnvilConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger( Main.class );

    public static void main( String[] args ) {
        LOGGER.info( "Anvil to levelDB converter from Gomint" );

        AssetsLibrary assets = new AssetsLibrary( null );

        try {
            assets.load( Main.class.getResourceAsStream( "/assets.dat" ) );
        } catch ( IOException e ) {
            LOGGER.warn( "Could not load needed data from assets.dat", e );
            return;
        }

        ClassPath classPath;
        try {
            classPath = new ClassPath( "io.gomint.server" );
        } catch ( IOException e ) {
            LOGGER.warn( "Could not create class path scanner", e );
            return;
        }

        File[] folderContent = new File( "." ).listFiles();
        for ( File file : folderContent ) {
            if ( file.isDirectory() && new File( file, "region" ).exists() ) {
                LOGGER.info( "Start converting process for {}", file.getName() );

                AnvilConverter anvilConverter = new AnvilConverter( assets, new Items( classPath, assets.getJeTopeItems() ), file );
                anvilConverter.done();
            }
        }
    }

}
