package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.block.helper.ToolPresets;
import io.gomint.server.world.block.state.EnumBlockState;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.WoodType;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:planks" )
public class Wood extends Block implements io.gomint.world.block.BlockWood {

    private EnumBlockState<WoodType> variant = new EnumBlockState<>( this, WoodType.values() );

    @Override
    public String getBlockId() {
        return "minecraft:planks";
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return ToolPresets.AXE;
    }

    @Override
    public float getBlastResistance() {
        return 15.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.WOOD;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public WoodType getWoodType() {
        return this.variant.getState();
    }

    @Override
    public void setWoodType( WoodType woodType ) {
        this.variant.setState( woodType );
    }

}
