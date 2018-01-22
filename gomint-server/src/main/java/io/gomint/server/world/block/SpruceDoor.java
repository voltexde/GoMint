package io.gomint.server.world.block;

import io.gomint.world.block.BlockType;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 193 )
public class SpruceDoor extends Door implements io.gomint.world.block.BlockSpruceDoor {

    @Override
    public byte getBlockId() {
        return (byte) 193;
    }

    @Override
    public float getBlastResistance() {
        return 15.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.SPRUCE_DOOR;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

}
