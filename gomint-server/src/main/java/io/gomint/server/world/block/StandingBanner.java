package io.gomint.server.world.block;

import io.gomint.world.block.BlockType;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 176 )
public class StandingBanner extends Banner {

    @Override
    public byte getBlockId() {
        return (byte) 176;
    }

    @Override
    public BlockType getType() {
        return BlockType.STANDING_BANNER;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

}
