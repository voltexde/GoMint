package io.gomint.server.world.converter.anvil;

import io.gomint.server.world.converter.BaseConverter;

import java.io.File;

/**
 * @author geNAZt
 * @version 1.0
 */
public class AnvilConverter extends BaseConverter {

    public AnvilConverter( File worldFolder ) {
        super( worldFolder );

        // Convert all region files first
        File regionFolder = new File( worldFolder, "region" );
        if ( regionFolder.exists() ) {
            convertRegionFiles( regionFolder );
        }
    }

    private void convertRegionFiles( File regionFolder ) {
        File[] regionFiles = regionFolder.listFiles( ( dir, name ) -> name.endsWith( ".mca" ) );
        if ( regionFiles == null ) {
            return;
        }

        // Iterate over all region files and check if they match the pattern
        for ( File regionFile : regionFiles ) {
            String fileName = regionFile.getName();
            if ( fileName.startsWith( "r." ) ) {
                String[] split = fileName.split( "\\." );
                if ( split.length != 4 ) {
                    continue;
                }

                int regionX = Integer.parseInt( split[1] );
                int regionZ = Integer.parseInt( split[2] );

                System.out.println( "Found region " + regionX + " " + regionZ );
            }
        }
    }

}
