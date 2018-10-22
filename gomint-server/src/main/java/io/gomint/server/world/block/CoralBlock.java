package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.block.helper.ToolPresets;
import io.gomint.server.world.block.state.EnumBlockState;
import io.gomint.world.block.BlockCoralBlock;
import io.gomint.world.block.BlockType;

/**
 * @author Kaooot
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:coral_block" )
public class CoralBlock extends Block implements BlockCoralBlock {

    private EnumBlockState<CoralType> variant = new EnumBlockState<>( this, CoralType.values() );

    @Override
    public String getBlockId() {
        return "minecraft:coral_block";
    }

    @Override
    public long getBreakTime() {
        return 50;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return ToolPresets.PICKAXE;
    }

    @Override
    public float getBlastResistance() {
        return 30;
    }

    @Override
    public BlockType getType() {
        return BlockType.CORAL_BLOCK;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public CoralType getCoralType() {
        return this.variant.getState();
    }

    @Override
    public void setCoralType( CoralType coralType ) {
        this.variant.setState( coralType );
    }

}
