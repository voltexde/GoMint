/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.math;

import io.gomint.world.World;
import lombok.Getter;

/**
 * <p>
 * A Location defines a point a world with three coordinates
 * </p>
 * Created by Markus on 21.10.2015.
 */
public class Location extends Vector implements Cloneable {
    @Getter private final World world;

    public Location( World world, double x, double y, double z ) {
        super( x, y, z );
        this.world = world;
    }

    public Location( World world, Vector vector ) {
        super( vector.getX(), vector.getY(), vector.getZ() );
        this.world = world;
    }

    @Override
    public Location add( Vector other ) {
        Vector vector = super.add( other );
        return new Location( this.world, vector );
    }

    @Override
    public Location subtract( Vector other ) {
        Vector vector = super.subtract( other );
        return new Location( this.world, vector );
    }

    @Override
    public Location multiply( Vector other ) {
        Vector vector = super.multiply( other );
        return new Location( this.world, vector );
    }

    @Override
    public Location divide( Vector other ) {
        Vector vector = super.divide( other );
        return new Location( this.world, vector );
    }

    @Override
    public Location scalar( float scalar ) {
        Vector vector = super.scalar( scalar );
        return new Location( this.world, vector );
    }

    @Override
    public String toString() {
        return String.format( "Location{world=%s, x=%s, y=%s, z=%s}", world.getLevelName(), x, y, z );
    }

    @Override
    public Location clone() {
        return new Location( world, x, y, z );
    }
}
