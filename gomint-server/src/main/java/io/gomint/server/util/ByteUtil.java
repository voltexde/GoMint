package io.gomint.server.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ByteUtil {

    public static void writeVarInt( int value, OutputStream stream ) throws IOException {
        while ( ( value & -128 ) != 0 ) {
            stream.write( value & 127 | 128 );
            value >>>= 7;
        }

        stream.write( value );
    }

}
