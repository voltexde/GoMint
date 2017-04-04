package io.gomint.server.world.anvil;

import io.gomint.taglib.NBTTagCompound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TileEntityConverter {

    public static void cleanSignText( NBTTagCompound blockEntityTag, String tagName ) {
        String text = blockEntityTag.getString( tagName, "" );
        if ( text != null ) {
            if ( text.startsWith( "{\"text" ) || text.startsWith( "{\"extra" ) ) {
                try {
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse( text );

                    StringBuilder output = new StringBuilder();
                    Object extraDataObject = jsonObject.get( "extra" );
                    if ( extraDataObject != null ) {
                        JSONArray extraData = (JSONArray) extraDataObject;
                        for ( Object extra : extraData ) {
                            parseInner( output, extra );
                        }
                    }

                    output.append( jsonObject.get( "text" ) );

                    blockEntityTag.addValue( tagName, output.toString() );
                } catch ( ParseException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void parseInner( StringBuilder output, Object extra ) {
        if ( extra instanceof String ) {
            output.append( (String) extra );
        } else if ( extra instanceof JSONObject ) {
            // This also can be a JsonObject containing text and extra
            JSONObject innerObj = (JSONObject) extra;
            if ( innerObj.containsKey( "text" ) ) {
                output.append( innerObj.get( "text" ) );
            }

            if ( innerObj.containsKey( "extra" ) ) {
                parseInner( output, innerObj.get( "extra" ) );
            }
        }
    }

}
