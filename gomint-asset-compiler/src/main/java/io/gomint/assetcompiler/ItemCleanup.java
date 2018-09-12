/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.assetcompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ItemCleanup {

    public static void main( String[] args ) throws IOException {
        for ( String line : Files.readAllLines( Paths.get( "cleanup.txt" ) ) ) {
            if ( !line.isEmpty() ) {
                // We have lines with pictures in front
                String[] split = line.split( "\\t" );
                if ( split[0].equals( "Icon" ) ) {
                    continue;
                }

                if ( !split[0].endsWith( ".png" ) ) {
                    // We are only interested in the dec representation and the final mc ID
                    System.out.println( split[0] + " minecraft:" + split[2] );
                } else {
                    System.out.println( split[1] + " minecraft:" + split[3] );
                }
            }
        }
    }

}
