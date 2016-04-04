/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class TileEntity {

    // CHECKSTYLE:OFF
    protected int x;
    protected int y;
    protected int z;
    // CHECKSTYLE:ON

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     */
    public TileEntity( NBTTagCompound tagCompound ) {
        this.x = tagCompound.getInteger( "x", 0 );
        this.y = tagCompound.getInteger( "y", -1 );
        this.z = tagCompound.getInteger( "z", 0 );
    }

    /**
     * Tick this tileEntity
     *
     * @param currentMillis The amount of millis to save some CPU
     */
    public abstract void tick( long currentMillis );

    /**
     * Save this TileEntity back to an compound
     *
     * @param compound The Compound which should be used to save the data into
     */
    public void toCompund( NBTTagCompound compound ) {
        compound.addValue( "x", this.x );
        compound.addValue( "y", this.y );
        compound.addValue( "z", this.z );
    }

}
