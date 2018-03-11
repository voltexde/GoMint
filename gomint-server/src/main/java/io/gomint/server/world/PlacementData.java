package io.gomint.server.world;

import io.gomint.taglib.NBTTagCompound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author geNAZt
 * @version 1.0
 */
@AllArgsConstructor
@Data
@Accessors( chain = true )
public class PlacementData {

    private byte blockId;
    private byte metaData;
    private NBTTagCompound compound;

}
