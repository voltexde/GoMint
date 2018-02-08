/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.v1_8;

import io.gomint.math.Location;
import io.gomint.server.entity.tileentity.SignTileEntity;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SignConverter extends BasisConverter<SignTileEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger( SignConverter.class );
    private static final JSONParser JSON_PARSER = new JSONParser();

    /**
     * Construct new converter
     *
     * @param worldAdapter for which we construct
     */
    public SignConverter( WorldAdapter worldAdapter ) {
        super( worldAdapter );
    }

    @Override
    public SignTileEntity readFrom( NBTTagCompound compound ) {
        // Read position first
        Location position = this.getPosition( compound );

        // Clean sign text
        String[] lines = new String[4];
        lines[0] = this.cleanText( compound, "Text1" );
        lines[1] = this.cleanText( compound, "Text2" );
        lines[2] = this.cleanText( compound, "Text3" );
        lines[3] = this.cleanText( compound, "Text4" );

        // Construct new sign
        return new SignTileEntity( lines, position );
    }

    @Override
    public void writeTo( SignTileEntity entity, NBTTagCompound compound ) {
        // Write basic stuff
        compound.addValue( "id", "Sign" );
        writePosition( entity.getLocation(), compound );

        // Write the lines
        for ( int i = 0; i < entity.getLines().size(); i++ ) {
            String line = entity.getLines().get( i );
            if ( line == null ) {
                line = "";
            }

            JSONObject object = new JSONObject();
            object.put( "text", line );
            compound.addValue( "Text" + (i + 1), object.toJSONString() );
        }
    }

    private String cleanText( NBTTagCompound compound, String tagName ) {
        String text = compound.getString( tagName, "" );
        if ( text != null && ( text.startsWith( "{\"text" ) || text.startsWith( "{\"extra" ) ) ) {
            try {
                JSONObject jsonObject = (JSONObject) JSON_PARSER.parse( text );

                StringBuilder output = new StringBuilder();
                Object extraDataObject = jsonObject.get( "extra" );
                if ( extraDataObject != null ) {
                    JSONArray extraData = (JSONArray) extraDataObject;
                    for ( Object extra : extraData ) {
                        parseInner( output, extra );
                    }
                }

                output.append( jsonObject.get( "text" ) );

                String tempOutput = output.toString();
                if ( tempOutput.length() == 2 && tempOutput.equals( "\"\"" ) ) {
                    return "";
                }

                return tempOutput;
            } catch ( ParseException e ) {
                LOGGER.warn( "Could not parse sign content for {}", tagName, e );
            }
        }

        return text;
    }

    private void parseInner( StringBuilder output, Object extra ) {
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
