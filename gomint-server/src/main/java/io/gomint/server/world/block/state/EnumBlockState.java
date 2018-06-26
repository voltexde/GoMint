/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.block.state;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnumBlockState<E extends Enum<E>> extends BlockState<E> {

    private E[] enumValues;

    public EnumBlockState( E[] values ) {
        this.enumValues = values;
        this.setState( values[0] );
    }

    @Override
    public byte toData() {
        return (byte) this.getState().ordinal();
    }

    @Override
    public void fromData( byte data ) {
        this.setState( this.enumValues[data] );
    }

}
