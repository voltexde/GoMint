package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.AxisAlignedBB;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.block.helper.ToolPresets;
import io.gomint.server.world.block.state.BooleanBlockState;
import io.gomint.server.world.block.state.FacingBlockState;
import io.gomint.world.block.BlockFace;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.Facing;

import java.util.Collections;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:trapdoor" )
public class Trapdoor extends Block implements io.gomint.world.block.BlockTrapdoor {

    private FacingBlockState facing = new FacingBlockState( this );
    private BooleanBlockState top = new BooleanBlockState( this, states -> true, 2 );
    private BooleanBlockState open = new BooleanBlockState( this, states -> true, 3 );

    @Override
    public boolean isOpen() {
        return this.open.getState();
    }

    @Override
    public void toggle() {
        this.open.setState( !this.isOpen() );
    }

    @Override
    public boolean interact( Entity entity, BlockFace face, Vector facePos, ItemStack item ) {
        toggle();
        return true;
    }

    @Override
    public String getBlockId() {
        return "minecraft:trapdoor";
    }

    @Override
    public long getBreakTime() {
        return 4500;
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
        return BlockType.TRAPDOOR;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public List<AxisAlignedBB> getBoundingBox() {
        float defaultHeight = 0.1875f;

        // Basis box
        AxisAlignedBB bb;
        if ( this.top.getState() ) {
            // Top closed box
            bb = new AxisAlignedBB(
                this.location.getX(),
                this.location.getY() + 1 - defaultHeight,
                this.location.getZ(),
                this.location.getX() + 1,
                this.location.getY() + 1,
                this.location.getZ() + 1
            );
        } else {
            // Bottom closed box
            bb = new AxisAlignedBB(
                this.location.getX(),
                this.location.getY(),
                this.location.getZ(),
                this.location.getX() + 1,
                this.location.getY() + defaultHeight,
                this.location.getZ() + 1
            );
        }

        // Check for open state
        if ( this.open.getState() ) {
            switch ( this.facing.getState() ) {
                case NORTH:
                    bb.setBounds(
                        this.location.getX(),
                        this.location.getY(),
                        this.location.getZ() + 1 - defaultHeight,
                        this.location.getX() + 1,
                        this.location.getY() + 1,
                        this.location.getZ() + 1
                    );

                    break;

                case SOUTH:
                    bb.setBounds(
                        this.location.getX(),
                        this.location.getY(),
                        this.location.getZ(),
                        this.location.getX() + 1,
                        this.location.getY() + 1,
                        this.location.getZ() + defaultHeight
                    );

                    break;

                case WEST:
                    bb.setBounds(
                        this.location.getX() + 1 - defaultHeight,
                        this.location.getY(),
                        this.location.getZ(),
                        this.location.getX() + 1,
                        this.location.getY() + 1,
                        this.location.getZ() + 1
                    );

                    break;

                case EAST:
                    bb.setBounds(
                        this.location.getX(),
                        this.location.getY(),
                        this.location.getZ(),
                        this.location.getX() + defaultHeight,
                        this.location.getY() + 1,
                        this.location.getZ() + 1
                    );

                    break;
            }
        }

        return Collections.singletonList( bb );
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return ToolPresets.AXE;
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
