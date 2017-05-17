package io.gomint.helper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockGenerator {

    public static void main( String[] args ) {
        String clazzTemplate = "package io.gomint.server.world.block;\n" +
                "\n" +
                "/**\n" +
                " * @author geNAZt\n" +
                " * @version 1.0\n" +
                " */\n" +
                "public class %blockName% extends Block {\n" +
                "\n" +
                "    @Override\n" +
                "    public int getBlockId() {\n" +
                "        return %blockId%;\n" +
                "    }\n";

        String templateEnd = "\n" +
                "}\n";

        // Download latest 1.0 block data
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod( "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pe/1.0/blocks.json" );

        try {
            client.executeMethod( method );
            byte[] responseBody = method.getResponseBody();

            JSONArray arrayOfBlocks = (JSONArray) new JSONParser().parse( new String( responseBody ) );
            for ( Object block : arrayOfBlocks ) {
                JSONObject blockObj = (JSONObject) block;

                String className = buildName( (String) blockObj.get( "displayName" ) );
                StringBuilder classBody = new StringBuilder( clazzTemplate
                        .replaceAll( "%blockName%", className )
                        .replace( "%blockId%", String.valueOf( blockObj.get( "id" ) ) ) );

                long timeToBreak = blockObj.get( "hardness" ) == null ? -1 : Math.round( Double.parseDouble( String.valueOf( blockObj.get( "hardness" ) ) ) * 1.5 * 1000 );
                if ( timeToBreak > 250 || timeToBreak == -1 ) {
                    classBody.append( "\n    @Override\n    public long getBreakTime() {\n        return " ).append( timeToBreak ).append( ";\n" ).append( "    }\n" );
                }

                if ( (boolean) blockObj.get( "transparent" ) ) {
                    classBody.append( "\n    @Override\n    public boolean isTransparent() {\n        return true;\n" ).append( "    }\n" );
                }

                if ( Objects.equals( blockObj.get( "boundingBox" ), "empty" ) ) {
                    classBody.append( "\n    @Override\n    public boolean isSolid() {\n        return false;\n" ).append( "    }\n" );
                }

                classBody.append( templateEnd );

                try ( FileWriter writer = new FileWriter( "generated/" + className + ".java" ) ) {
                    writer.write( classBody.toString() );
                }

                String constantName = ( (String) blockObj.get( "name" ) ).toUpperCase();
                System.out.println( constantName + "(" + String.valueOf( blockObj.get( "id" ) ) + ", \"minecraft:" + blockObj.get( "name" ) + "\")," );
                // System.out.println( "public static final " + className + " " + constantName + " = createBlock( " + String.valueOf( blockObj.get( "id" ) ) + ", " + className + ".class );" );
            }
        } catch ( IOException | ParseException e ) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
    }

    private static String buildName( String displayName ) {
        String[] temp = displayName.split( " " );
        StringBuilder newName = new StringBuilder();

        for ( int i = 0; i < temp.length; i++ ) {
            String old = temp[i];
            if ( old.contains( "(" ) ) {
                old = old.replaceAll( "\\(", "" );
                old = old.replaceAll( "\\)", "" );
            }

            String part = WordUtils.capitalize( old.replaceAll( "'", "" ) );
            newName.append( part );
        }

        return newName.toString();
    }

}
