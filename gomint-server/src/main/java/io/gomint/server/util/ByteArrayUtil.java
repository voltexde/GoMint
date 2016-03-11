/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import java.nio.ByteOrder;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ByteArrayUtil {
	public static byte[] intToByteArray( int value, ByteOrder byteOrder ) {
		if ( byteOrder == ByteOrder.BIG_ENDIAN ) {
			return new byte[] {
					(byte) ( value >> 24 ),
					(byte) ( value >> 16 ),
					(byte) ( value >> 8 ),
					(byte) value
			};
		} else {
			return new byte[] {
					(byte) value,
					(byte) ( value >> 8 ),
					(byte) ( value >> 16 ),
					(byte) ( value >> 24 )
			};
		}
	}

	public static byte[] shortToByteArray( short value, ByteOrder byteOrder ) {
		if ( byteOrder == ByteOrder.BIG_ENDIAN ) {
			return new byte[] {
					(byte) ( value >> 8 ),
					(byte) value
			};
		} else {
			return new byte[] {
					(byte) value,
					(byte) ( value >> 8 )
			};
		}
	}
}
