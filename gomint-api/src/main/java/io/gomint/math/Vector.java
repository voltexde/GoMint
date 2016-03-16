/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.math;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * An immutable class to represent a x, y and z coordinate
 * </p>
 *
 * @author Digot
 * @author geNAZt
 * @version 1.1
 */
public class Vector implements Cloneable {
    public static final Vector ZERO = new Vector( 0, 0, 0 );

    @Getter @Setter protected double x;
    @Getter @Setter protected double y;
    @Getter @Setter protected double z;

    public Vector( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add( double x, double y, double z ) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public Vector subtract( double x, double y, double z ) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Vector multiply( double x, double y, double z ) {
        this.x *= x;
        this.y *= y;
        this.z *= z;

        return this;
    }

    public Vector divide( double x, double y, double z ) {
        this.x /= x;
        this.y /= y;
        this.z /= z;

        return this;
    }

    public Vector scalar( float value ) {
        this.x *= value;
        this.y *= value;
        this.z *= value;

        return this;
    }

    @Override
    public Vector clone() {
        return new Vector( x, y, z );
    }
}
