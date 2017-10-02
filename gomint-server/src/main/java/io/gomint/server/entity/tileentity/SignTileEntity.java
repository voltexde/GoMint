/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import com.google.common.base.Joiner;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
class SignTileEntity extends TileEntity {

    private List<String> lines = new ArrayList<>( 4 );

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    public SignTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );

        if ( tagCompound.containsKey( "Text" ) ) {
            String text = tagCompound.getString( "Text", "" );
            this.lines.addAll( Arrays.asList( text.split( "\n" ) ) );
        } else {
            // This is the Anvil version
            this.lines.add( tagCompound.getString( "Text1", "" ) );
            this.lines.add( tagCompound.getString( "Text2", "" ) );
            this.lines.add( tagCompound.getString( "Text3", "" ) );
            this.lines.add( tagCompound.getString( "Text4", "" ) );
        }
    }

    @Override
    public void update( long currentMillis, float dF ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound ) {
        super.toCompound( compound );

        compound.addValue( "id", "Sign" );
        compound.addValue( "Text", Joiner.on( "\n" ).join( this.lines ) );
    }

}
