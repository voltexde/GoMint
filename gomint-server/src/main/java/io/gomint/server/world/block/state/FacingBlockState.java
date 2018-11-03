/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.block.state;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.Vector;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.util.Bearing;
import io.gomint.server.world.block.Block;
import io.gomint.world.block.BlockFace;
import io.gomint.world.block.data.Facing;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author geNAZt
 * @version 1.0
 */
public class FacingBlockState extends BlockState<Facing> {

    private final short rotation;

    public FacingBlockState( Block block ) {
        super( block );
        this.rotation = 0;
    }

    public FacingBlockState( Block block, Predicate<List<BlockState>> predicate ) {
        super( block, predicate );
        this.rotation = 0;
    }

    public FacingBlockState( Block block, Predicate<List<BlockState>> predicate, int shift ) {
        super( block, predicate, shift );
        this.rotation = 0;
    }

    public FacingBlockState( Block block, short rotation ) {
        super( block );
        this.rotation = rotation;
    }

    public FacingBlockState( Block block, Predicate<List<BlockState>> predicate, short rotation ) {
        super( block, predicate );
        this.rotation = rotation;
    }

    public FacingBlockState( Block block, Predicate<List<BlockState>> predicate, int shift, short rotation ) {
        super( block, predicate, shift );
        this.rotation = rotation;
    }

    @Override
    protected int cap() {
        return 3;
    }

    @Override
    public void detectFromPlacement( EntityPlayer player, ItemStack placedItem, BlockFace face, Block block, Block clickedBlock, Vector clickPosition ) {
        Bearing bearing = Bearing.fromAngle( player.getYaw() );
        this.setState( bearing.toFacing() );
    }

    @Override
    protected void data( short data ) {
        data -= this.rotation;
        data &= this.cap();

        switch ( data ) {
            case 3:
                this.setState( Facing.EAST );
                break;
            case 1:
                this.setState( Facing.WEST );
                break;
            case 0:
                this.setState( Facing.SOUTH );
                break;
            case 2:
                this.setState( Facing.NORTH );
                break;
            default:
                this.setState( Facing.EAST );
        }
    }

    @Override
    protected short data() {
        short data = 0;
        switch ( this.getState() ) {
            case NORTH:
                data = 2;
                break;
            case EAST:
                data = 3;
                break;
            case WEST:
                data = 1;
                break;
            case SOUTH:
            default:
                break;
        }

        // Rotate
        data += this.rotation;
        data &= this.cap();
        return data;
    }

}
