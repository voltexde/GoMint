package io.gomint.server.world.converter.anvil;

import io.gomint.server.world.converter.BaseConverter;
import io.gomint.taglib.NBTTagCompound;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
        long start = System.nanoTime();
        for ( File regionFile : regionFiles ) {
            String fileName = regionFile.getName();
            if ( fileName.startsWith( "r." ) ) {
                String[] split = fileName.split( "\\." );
                if ( split.length != 4 ) {
                    continue;
                }


                try {
                    RegionFileSingleChunk regionFileReader = new RegionFileSingleChunk( regionFile );

                    for ( int x = 0; x < 32; x++ ) {
                        for ( int z = 0; z < 32; z++ ) {
                            NBTTagCompound compound = regionFileReader.loadChunk( x, z );
                            if ( compound == null ) {
                                continue;
                            }

                            NBTTagCompound levelCompound = compound.getCompound( "Level", false );
                            System.out.println( levelCompound );
                        }
                    }
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println( "Done in " + TimeUnit.NANOSECONDS.toMillis( System.nanoTime() - start ) + " ms" );
    }

}
