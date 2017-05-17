package io.gomint.helper;

/**
 * @author geNAZt
 * @version 1.0
 */
public class VarIntPredictionGenerator {

    public static void main( String[] args ) {
        for ( int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++ ) {
            int size = 1;
            int value = i;
            while ( ( value & -128 ) != 0 ) {
                value >>>= 7;
                size++;
            }

            System.out.println( i + " -> " + size );
        }
    }

}
