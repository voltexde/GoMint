/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.util;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Numbers {

    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5;

    public static double square( double in ) {
        return in * in;
    }

    public static int fastFloor( float x ) {
        return (int) ( x + BIG_ENOUGH_FLOOR ) - BIG_ENOUGH_INT;
    }

    public static int fastRound( float x ) {
        return (int) ( x + BIG_ENOUGH_ROUND ) - BIG_ENOUGH_INT;
    }

    public static int fastCeil( float x ) {
        return BIG_ENOUGH_INT - (int) ( BIG_ENOUGH_FLOOR - x );
    }

    public static boolean roughlyEquals( double a, double b, double threshold ) {
        double diff = Math.abs( a - b );
        return diff <= threshold;
    }

}
