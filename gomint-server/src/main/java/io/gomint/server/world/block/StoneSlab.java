package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.world.block.helper.ToolPresets;
import io.gomint.server.world.block.state.EnumBlockState;
import io.gomint.world.block.BlockStoneSlab;
import io.gomint.world.block.BlockType;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.world.block.data.StoneType;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:stone_slab" )
@RegisterInfo( sId = "minecraft:stone_slab2" )
public class StoneSlab extends Slab implements BlockStoneSlab {

    private enum StoneType1 {
        STONE,
        SANDSTONE,
        COBBLESTONE,
        BRICKS,
        STONE_BRICK,
        NETHER_BRICK,
        QUARTZ
    }

    private enum StoneType2 {
        RED_SANDSTONE,
        PURPUR,
        PRISMARINE,
        PRISMARINE_BRICK
    }

    private EnumBlockState<StoneType1> variant = new EnumBlockState<>( this, StoneType1.values(), states -> this.getBlockId().equals( "minecraft:stone_slab" ) );
    private EnumBlockState<StoneType2> variant2 = new EnumBlockState<>( this, StoneType2.values(), states -> this.getBlockId().equals( "minecraft:stone_slab2" ) );

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
        return 30.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.STONE_SLAB;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return ToolPresets.PICKAXE;
    }

    @Override
    public StoneType getStoneType() {
        switch ( this.getBlockId() ) {
            case "minecraft:stone_slab":
                switch ( this.variant.getState() ) {
                    case STONE:
                        return StoneType.STONE;
                    case SANDSTONE:
                        return StoneType.SANDSTONE;
                    case COBBLESTONE:
                        return StoneType.COBBLESTONE;
                    case BRICKS:
                        return StoneType.BRICKS;
                    case QUARTZ:
                        return StoneType.QUARTZ;
                    case STONE_BRICK:
                        return StoneType.STONE_BRICK;
                    case NETHER_BRICK:
                        return StoneType.NETHER_BRICK;
                }

                break;
            case "minecraft:stone_slab2":
                switch ( this.variant2.getState() ) {
                    case PURPUR:
                        return StoneType.PURPUR;
                    case PRISMARINE:
                        return StoneType.PRISMARINE;
                    case RED_SANDSTONE:
                        return StoneType.RED_SANDSTONE;
                    case PRISMARINE_BRICK:
                        return StoneType.PRISMARINE_BRICK;
                }

                break;
        }

        return null;
    }

    @Override
    public void setStoneType( StoneType stoneType ) {
        switch ( stoneType ) {
            case STONE:
                this.setBlockId( "minecraft:stone_slab" );
                this.variant.setState( StoneType1.STONE );
                break;
            case SANDSTONE:
                this.setBlockId( "minecraft:stone_slab" );
                this.variant.setState( StoneType1.SANDSTONE );
                break;
            case COBBLESTONE:
                this.setBlockId( "minecraft:stone_slab" );
                this.variant.setState( StoneType1.COBBLESTONE );
                break;
            case BRICKS:
                this.setBlockId( "minecraft:stone_slab" );
                this.variant.setState( StoneType1.BRICKS );
                break;
            case QUARTZ:
                this.setBlockId( "minecraft:stone_slab" );
                this.variant.setState( StoneType1.QUARTZ );
                break;
            case STONE_BRICK:
                this.setBlockId( "minecraft:stone_slab" );
                this.variant.setState( StoneType1.STONE_BRICK );
                break;
            case NETHER_BRICK:
                this.setBlockId( "minecraft:stone_slab" );
                this.variant.setState( StoneType1.NETHER_BRICK );
                break;
            case PURPUR:
                this.setBlockId( "minecraft:stone_slab2" );
                this.variant2.setState( StoneType2.PURPUR );
                break;
            case PRISMARINE:
                this.setBlockId( "minecraft:stone_slab2" );
                this.variant2.setState( StoneType2.PRISMARINE );
                break;
            case RED_SANDSTONE:
                this.setBlockId( "minecraft:stone_slab2" );
                this.variant2.setState( StoneType2.RED_SANDSTONE );
                break;
            case PRISMARINE_BRICK:
                this.setBlockId( "minecraft:stone_slab2" );
                this.variant2.setState( StoneType2.PRISMARINE_BRICK );
                break;
        }
    }

}
