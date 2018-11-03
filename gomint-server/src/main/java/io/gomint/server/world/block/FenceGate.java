/*
 * Copyright (c) 2018 Gomint Team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.world.block.helper.ToolPresets;
import io.gomint.server.world.block.state.BooleanBlockState;
import io.gomint.server.world.block.state.FacingBlockState;
import io.gomint.world.block.BlockType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.world.block.data.Facing;
import io.gomint.world.block.data.WoodType;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:fence_gate", def = true )
@RegisterInfo( sId = "minecraft:spruce_fence_gate" )
@RegisterInfo( sId = "minecraft:birch_fence_gate" )
@RegisterInfo( sId = "minecraft:jungle_fence_gate" )
@RegisterInfo( sId = "minecraft:dark_oak_fence_gate" )
@RegisterInfo( sId = "minecraft:acacia_fence_gate" )
public class FenceGate extends Block implements io.gomint.world.block.BlockFenceGate {

    private final FacingBlockState facing = new FacingBlockState( this );
    private final BooleanBlockState open = new BooleanBlockState( this, blockStates -> true, 2 );

    @Override
    public String getBlockId() {
        return "minecraft:fence_gate";
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public float getBlastResistance() {
        return 15.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.FENCE_GATE;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return ToolPresets.AXE;
    }

    @Override
    public void toggle() {
        this.open.setState( !this.isOpen() );
    }

    @Override
    public boolean isOpen() {
        return this.open.getState();
    }

    @Override
    public WoodType getWoodType() {
        switch ( this.getBlockId() ) {
            case "minecraft:fence_gate":
                return WoodType.OAK;
            case "minecraft:spruce_fence_gate":
                return WoodType.SPRUCE;
            case "minecraft:birch_fence_gate":
                return WoodType.BIRCH;
            case "minecraft:jungle_fence_gate":
                return WoodType.DARK_OAK;
            case "minecraft:dark_oak_fence_gate":
                return WoodType.JUNGLE;
            case "minecraft:acacia_fence_gate":
                return WoodType.ACACIA;
        }

        return WoodType.OAK;
    }

    @Override
    public void setWoodType( WoodType woodType ) {
        switch ( woodType ) {
            case OAK:
                this.setBlockId( "minecraft:fence_gate" );
                break;
            case SPRUCE:
                this.setBlockId( "minecraft:spruce_fence_gate" );
                break;
            case BIRCH:
                this.setBlockId( "minecraft:birch_fence_gate" );
                break;
            case DARK_OAK:
                this.setBlockId( "minecraft:dark_oak_fence_gate" );
                break;
            case JUNGLE:
                this.setBlockId( "minecraft:jungle_fence_gate" );
                break;
            case ACACIA:
                this.setBlockId( "minecraft:acacia_fence_gate" );
                break;
        }
    }

    @Override
    public void setFacing( Facing facing ) {
        this.facing.setState( facing );
    }

    @Override
    public Facing getFacing() {
        return this.facing.getState();
    }

}
