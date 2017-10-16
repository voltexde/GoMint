package io.gomint.server.world;

import io.gomint.taglib.NBTTagCompound;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public class PlacementData {

    private final byte metaData;
    private final NBTTagCompound compound;

}
