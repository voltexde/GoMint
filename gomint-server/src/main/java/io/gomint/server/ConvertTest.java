package io.gomint.server;

import io.gomint.server.assets.AssetsLibrary;
import io.gomint.server.world.converter.anvil.AnvilConverter;

import java.io.File;
import java.io.IOException;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ConvertTest {

    public static void main( String[] args ) {
        AssetsLibrary assetsLibrary = new AssetsLibrary( null );

        try {
            assetsLibrary.load( ConvertTest.class.getResourceAsStream( "/assets.dat" ) );
        } catch ( IOException e ) {
            return;
        }

        AnvilConverter converter = new AnvilConverter( assetsLibrary, new File( "testworld" ) );
        converter.done();
    }

}
