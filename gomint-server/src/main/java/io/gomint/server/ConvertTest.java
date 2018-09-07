package io.gomint.server;

import io.gomint.server.world.converter.anvil.AnvilConverter;

import java.io.File;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ConvertTest {

    public static void main( String[] args ) {
        AnvilConverter converter = new AnvilConverter( new File( "testworld" ) );
    }

}
