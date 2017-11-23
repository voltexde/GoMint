/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TypeGenerator {

    public static void main( String[] args ) throws IOException {
        List<String> enumConstants = new ArrayList<>();

        File itemFolder = new File( "gomint-server/src/main/java/io/gomint/server/world/block/" );
        for ( File file : itemFolder.listFiles( new FilenameFilter() {
            @Override
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".java" );
            }
        } ) ) {
            String content = new String( Files.readAllBytes( file.toPath() ) );
            if ( content.contains( "@RegisterInfo" ) ) {
                String enumConstant = getConstantName( file.getName() );
                enumConstants.add( enumConstant );

                int con = content.length();
                while ( con-- > 0 ) {
                    if ( content.charAt( con ) == '}' ) {
                        break;
                    }
                }

                String append = content.substring( 0, con );

                // Check for missing interface
                if ( !content.contains( "implements" ) ) {

                }

                append = append.replace( "package io.gomint.server.world.block;", "package io.gomint.server.world.block\n\nimport io.gomint.world.block.BlockType;" );
                append += "    @Override\n    public BlockType getType() {\n        return BlockType." + enumConstant + ";\n    }\n\n}";

                // Files.write( file.toPath(), append.getBytes(), StandardOpenOption.TRUNCATE_EXISTING );

                System.out.println( append );
            }
        }

        /*System.out.println( "public enum BlockType { " );
        for ( String enumConstant : enumConstants ) {
            System.out.println( "    " + enumConstant + "," );
        }
        System.out.println( "}" );*/
    }

    private static String getConstantName( String name ) {
        name = name.replace( ".java", "" );

        String enumConstant = "";
        for ( int i = 0; i < name.length(); i++ ) {
            char c = name.charAt( i );
            if ( c <= 90 && c >= 65 ) {
                if ( i > 0 ) {
                    enumConstant += "_";
                }

                enumConstant += c;
            } else if ( c <= 122 && c >= 97 ){
                enumConstant += (char) (c - 32);
            } else {
                enumConstant += c;
            }
        }

        return enumConstant;
    }

}
