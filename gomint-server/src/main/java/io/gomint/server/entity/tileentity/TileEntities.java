/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.taglib.NBTTagCompound;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * @author geNAZt
 * @version 1.0
 */
public enum TileEntities {

    /**
     * Represents a sign. This TileEntity holds 4 lines of strings to be displayed on a piece of wood
     */
    SIGN( "Sign", SignTileEntity.class );

    private final String nbtID;
    private final MethodHandle tileEntityConstructor;

    /**
     * Construct a new TileEntity enum value
     *
     * @param nbtID             The ID which can be found in NBT Tags
     * @param tileEntityClass   The class which should be used to instantiate the TileEntity
     */
    TileEntities( String nbtID, Class<? extends TileEntity> tileEntityClass ) {
        this.nbtID = nbtID;

        try {
            this.tileEntityConstructor = MethodHandles.lookup().unreflectConstructor( tileEntityClass.getConstructor( NBTTagCompound.class ) );
        } catch ( IllegalAccessException | NoSuchMethodException e ) {
            e.printStackTrace();
            this.tileEntityConstructor = null;
        }
    }

    /**
     * Construct a new TileEntity which then reads in the data from the given Compound
     *
     * @param compound  The compound from which the data should be read
     * @return The constructed and ready to use TileEntity or null
     */
    public TileEntity construct( NBTTagCompound compound ) {
        try {
            return (TileEntity) this.tileEntityConstructor.invoke( compound );
        } catch ( Throwable throwable ) {
            throwable.printStackTrace();
        }

        return null;
    }

}
