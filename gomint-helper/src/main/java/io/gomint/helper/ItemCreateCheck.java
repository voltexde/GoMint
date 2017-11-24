package io.gomint.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author geNAZt
 */
public class ItemCreateCheck {

    public static void main( String[] args ) {
        File itemFolder = new File( "gomint-api/src/main/java/io/gomint/inventory/item/" );
        for ( File file : itemFolder.listFiles( new FilenameFilter() {
            @Override
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".java" );
            }
        } ) ) {
            try {
                String content = new String( Files.readAllBytes( file.toPath() ) );
                if ( !content.contains( "create(" ) ) {
                    System.out.println( file.getAbsolutePath() );
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

}
