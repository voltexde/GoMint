/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockRuntimeIDs {

    private static final Logger LOGGER = LoggerFactory.getLogger( BlockRuntimeIDs.class );
    private static final Map<Pair<Byte, Byte>, Integer> RUNTIME_IDS = new ConcurrentHashMap<>();

    static {
        // Get the correct resource
        InputStream inputStream = BlockRuntimeIDs.class.getResourceAsStream( "/temp_runtimeids.json" );
        if ( inputStream == null ) {
            try {
                inputStream = new FileInputStream( "gomint-server/src/main/resources/temp_runtimeids.json" );
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
        }

        JSONParser parser = new JSONParser();

        try ( InputStreamReader reader = new InputStreamReader( inputStream ) ) {
            JSONArray runtimeIDs = (JSONArray) parser.parse( reader );
            for ( Object id : runtimeIDs ) {
                JSONObject idObj = (JSONObject) id;
                RUNTIME_IDS.put( new Pair<>( ( (Long) idObj.get( "id" ) ).byteValue(), ( (Long) idObj.get( "data" ) ).byteValue() ), ( (Long) idObj.get( "runtimeID" ) ).intValue() );
            }
        } catch ( IOException | ParseException e ) {
            e.printStackTrace();
        }
    }

    public static Integer fromLegacy( byte blockId, byte dataValue ) {
        Integer runtimeId = RUNTIME_IDS.get( new Pair<>( blockId, dataValue ) );
        if ( runtimeId == null ) {
            LOGGER.warn( "Unknown blockId and dataValue combination: {}:{}", blockId, dataValue, new Exception() );
        }

        return runtimeId;
    }

}
