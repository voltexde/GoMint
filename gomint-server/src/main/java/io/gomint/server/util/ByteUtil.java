package io.gomint.server.util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ByteUtil {

    public static void writeVarInt( int value, DataOutputStream stream ) throws IOException {
        while ( ( value & -128 ) != 0 ) {
            stream.writeByte( value & 127 | 128 );
            value >>>= 7;
        }

        stream.writeByte( value );
    }

    /**
     * @param v Signed int
     * @return Unsigned encoded int
     */
    public static long encodeZigZag32( int v ) {
        // Note:  the right-shift must be arithmetic
        return (long) ( ( v << 1 ) ^ ( v >> 31 ) );
    }

    /**
     * @param v Unsigned encoded int
     * @return Signed decoded int
     */
    public static int decodeZigZag32( long v ) {
        return (int) ( v >> 1 ) ^ -(int) ( v & 1 );
    }

}
