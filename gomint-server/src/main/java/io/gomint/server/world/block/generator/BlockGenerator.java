package io.gomint.server.world.block.generator;

import io.gomint.math.Location;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.world.block.Block;

/**
 * @author geNAZt
 */
public interface BlockGenerator {

    <T extends Block> T generate( byte blockData, byte skyLightLevel, byte blockLightLevel, TileEntity tileEntity, Location location );

    <T extends Block> T generate();

}
