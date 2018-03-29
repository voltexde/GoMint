/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil.entity.mcpe;

import io.gomint.server.entity.Entity;
import io.gomint.server.util.DumpUtil;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.anvil.entity.EntityConverters;
import io.gomint.taglib.NBTTagCompound;
import lombok.RequiredArgsConstructor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class Entities implements EntityConverters {

    private final WorldAdapter world;

    @Override
    public Entity read( NBTTagCompound compound ) {
        DumpUtil.dumpNBTCompund( compound );

        String id = compound.getString( "id", null );
        int entityId = -1;

        if ( id == null ) {

        }

        return null;
    }

}
