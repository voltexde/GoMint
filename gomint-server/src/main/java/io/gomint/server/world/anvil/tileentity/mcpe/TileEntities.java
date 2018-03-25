/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.tileentity.mcpe;

import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.anvil.tileentity.TileEntityConverters;
import io.gomint.taglib.NBTTagCompound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TileEntities implements TileEntityConverters {

    private static final Logger LOGGER = LoggerFactory.getLogger( TileEntities.class );
    private final WorldAdapter world;

    /**
     * Construct 1.8 converter for the given world
     *
     * @param worldAdapter which should be used in all convert operations
     */
    public TileEntities( WorldAdapter worldAdapter ) {
        this.world = worldAdapter;
    }

    @Override
    public TileEntity read( NBTTagCompound compound ) {
        return io.gomint.server.entity.tileentity.TileEntities.construct( compound, this.world );
    }

    @Override
    public NBTTagCompound write( TileEntity tileEntity ) {
        NBTTagCompound compound = new NBTTagCompound( "" );
        tileEntity.toCompound( compound );
        return compound;
    }

}
