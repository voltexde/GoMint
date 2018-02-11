package io.gomint.world;

import com.google.common.base.Preconditions;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ParticleData {

    // Colored particle
    private int r = -1;
    private int g = -1;
    private int b = -1;
    private int a = -1;

    private ParticleData( int r, int g, int b, int a ) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Get the red value. This will be -1 when not given
     *
     * @return red value
     */
    public int getRed() {
        return this.r;
    }

    /**
     * Get the green value. This will be -1 when not given
     *
     * @return green value
     */
    public int getGreen() {
        return this.g;
    }

    /**
     * Get the blue value. This will be -1 when not given
     *
     * @return blue value
     */
    public int getBlue() {
        return this.b;
    }

    /**
     * Get the alpha value. This will be -1 when not given
     *
     * @return alpha value
     */
    public int getAlpha() {
        return this.a;
    }

    /**
     * Create a new particle data based on the color given as arguments
     *
     * @param r amount of red
     * @param g amount of green
     * @param b amount of blue
     * @param a amount of alpha
     * @return particle data which can be used to send particles to the player
     * @throws IllegalArgumentException when one of the three values is outside of the range of 0 - 255
     */
    public static ParticleData color( int r, int g, int b, int a ) {
        Preconditions.checkArgument( r >= 0 && r <= 255, "Red is outside of 0 -> 255" );
        Preconditions.checkArgument( g >= 0 && g <= 255, "Green is outside of 0 -> 255" );
        Preconditions.checkArgument( b >= 0 && b <= 255, "Blue is outside of 0 -> 255" );
        Preconditions.checkArgument( a >= 0 && a <= 255, "Alpha is outside of 0 -> 255" );

        return new ParticleData( r, g, b, 255 );
    }

    /**
     * Create a new particle data based on the color given as arguments
     *
     * @param r amount of red
     * @param g amount of green
     * @param b amount of blue
     * @return particle data which can be used to send particles to the player
     * @throws IllegalArgumentException when one of the three values is outside of the range of 0 - 255
     */
    public static ParticleData color( int r, int g, int b ) {
        return ParticleData.color( r, g, b, 255 );
    }

}
