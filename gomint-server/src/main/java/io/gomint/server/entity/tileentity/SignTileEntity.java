/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.taglib.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SignTileEntity extends TileEntity {

    private List<String> lines = new ArrayList<>( 4 );

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     */
    public SignTileEntity( NBTTagCompound tagCompound ) {
        super( tagCompound );

        this.lines.add( tagCompound.getString( "Text1", "" ) );
        this.lines.add( tagCompound.getString( "Text2", "" ) );
        this.lines.add( tagCompound.getString( "Text3", "" ) );
        this.lines.add( tagCompound.getString( "Text4", "" ) );
    }

    @Override
    public void tick( long currentMillis ) {

    }

    @Override
    public void toCompund( NBTTagCompound compound ) {
        super.toCompund( compound );

        compound.addValue( "Text1", this.lines.get( 0 ) );
        compound.addValue( "Text2", this.lines.get( 1 ) );
        compound.addValue( "Text3", this.lines.get( 2 ) );
        compound.addValue( "Text4", this.lines.get( 3 ) );
    }

}
