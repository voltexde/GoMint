/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.math;

import lombok.Getter;

/**
 * <p>
 * An immutable class to represent a x, y and z coordinate
 * </p>
 *
 * @author Digot
 * @version 1.0
 */
public class Vector implements Cloneable {
    public static final Vector ZERO = new Vector( 0, 0, 0 );

    @Getter protected final double x;
    @Getter protected final double y;
    @Getter protected final double z;

    public Vector( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add( Vector other ) {
        return new Vector(
                this.x + other.getX(),
                this.y + other.getY(),
                this.z + other.getZ()
        );
    }

    public Vector subtract( Vector other ) {
        return new Vector(
                this.x - other.getX(),
                this.y - other.getY(),
                this.z - other.getZ()
        );
    }

    public Vector multiply( Vector other ) {
        return new Vector(
                this.x * other.getX(),
                this.y * other.getY(),
                this.z * other.getZ()
        );
    }

    public Vector divide( Vector other ) {
        return new Vector(
                this.x / other.getX(),
                this.y / other.getY(),
                this.z / other.getZ()
        );
    }

    public Vector scalar( float value ) {
        return new Vector(
                this.x * value,
                this.y * value,
                this.z * value
        );
    }

    @Override
    public Vector clone() {
        return new Vector( x, y, z );
    }
}
