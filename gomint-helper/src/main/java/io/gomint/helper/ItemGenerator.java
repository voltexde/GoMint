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
public class ItemGenerator {

    public static void main( String[] args ) {
        System.out.println( 4 >> 2 );

        // Download latest 1.0 block data
        /*HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod( "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pe/1.0/items.json" );

        try {
            client.executeMethod( method );
            byte[] responseBody = method.getResponseBody();

            JSONArray arrayOfItems = (JSONArray) new JSONParser().parse( new String( responseBody ) );
            for ( Object item : arrayOfItems ) {
                JSONObject itemObj = (JSONObject) item;

                String constantName = ( (String) itemObj.get( "name" ) ).toUpperCase();
                if ( 64 != (long) itemObj.get( "stackSize" ) ) {
                    System.out.println( constantName + " => " + itemObj.get( "stackSize" ) );
                }
                // System.out.println( "case \"minecraft:" + itemObj.get( "name" ) + "\": \n  return " + constantName + ";");
            }
        } catch ( IOException | ParseException e ) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }*/
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
