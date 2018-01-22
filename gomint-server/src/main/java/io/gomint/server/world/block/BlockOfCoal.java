package io.gomint.server.world.block;

import io.gomint.world.block.BlockType;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 173 )
public class BlockOfCoal extends Block implements io.gomint.world.block.BlockBlockOfCoal {

    @Override
    public byte getBlockId() {
        return (byte) 173;
    }

    @Override
    public long getBreakTime() {
        return 7500;
    }

    @Override
    public float getBlastResistance() {
        return 30.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.BLOCK_OF_COAL;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

}
