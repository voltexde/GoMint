package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.UpdateReason;
import io.gomint.server.world.block.helper.ToolPresets;
import io.gomint.server.world.block.state.BlockfaceBlockState;
import io.gomint.server.world.block.state.BooleanBlockState;
import io.gomint.world.block.BlockFace;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.WoodType;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:wooden_button", def = true )
@RegisterInfo( sId = "minecraft:spruce_button" )
@RegisterInfo( sId = "minecraft:birch_button" )
@RegisterInfo( sId = "minecraft:dark_oak_button" )
@RegisterInfo( sId = "minecraft:jungle_button" )
@RegisterInfo( sId = "minecraft:acacia_button" )
public class WoodenButton extends Block implements io.gomint.world.block.BlockWoodenButton {

    private BlockfaceBlockState facing = new BlockfaceBlockState( this, true );
    private BooleanBlockState pressed = new BooleanBlockState( this, states -> true, 3 );

    @Override
    public long getBreakTime() {
        return 750;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public boolean interact( Entity entity, BlockFace face, Vector facePos, ItemStack item ) {
        // Press the button
        this.press();

        return true;
    }

    @Override
    public long update( UpdateReason updateReason, long currentTimeMS, float dT ) {
        if ( updateReason == UpdateReason.SCHEDULED && isPressed() ) {
            this.pressed.setState( false );
        }

        return -1;
    }

    @Override
    public boolean isPressed() {
        return this.pressed.getState();
    }

    @Override
    public void press() {
        // Check if we need to update
        if ( !isPressed() ) {
            this.pressed.setState( true );
        }

        // Schedule release in 1 second
        this.world.scheduleBlockUpdate( this.location, 1, TimeUnit.SECONDS );
    }

    @Override
    public BlockFace getAttachedFace() {
        return this.facing.getState();
    }

    @Override
    public void setAttachedFace( BlockFace face ) {
        this.facing.setState( face );
    }

    @Override
    public WoodType getWoodType() {
        switch ( this.getBlockId() ) {
            case "minecraft:wooden_button":
                return WoodType.OAK;
            case "minecraft:spruce_button":
                return WoodType.SPRUCE;
            case "minecraft:birch_button":
                return WoodType.BIRCH;
            case "minecraft:dark_oak_button":
                return WoodType.DARK_OAK;
            case "minecraft:jungle_button":
                return WoodType.JUNGLE;
            case "minecraft:acacia_button":
                return WoodType.ACACIA;
        }

        return WoodType.OAK;
    }

    @Override
    public void setWoodType( WoodType woodType ) {
        switch ( woodType ) {
            case OAK:
                this.setBlockId( "minecraft:wooden_button" );
                break;
            case SPRUCE:
                this.setBlockId( "minecraft:spruce_button" );
                break;
            case BIRCH:
                this.setBlockId( "minecraft:birch_button" );
                break;
            case DARK_OAK:
                this.setBlockId( "minecraft:dark_oak_button" );
                break;
            case JUNGLE:
                this.setBlockId( "minecraft:jungle_button" );
                break;
            case ACACIA:
                this.setBlockId( "minecraft:acacia_button" );
                break;
        }
    }

    @Override
    public float getBlastResistance() {
        return 2.5f;
    }

    @Override
    public BlockType getType() {
        return BlockType.WOODEN_BUTTON;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return ToolPresets.AXE;
    }

}
