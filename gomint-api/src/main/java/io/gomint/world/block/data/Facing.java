package io.gomint.world.block.data;

import io.gomint.world.block.BlockFace;

/**
 * @author geNAZt
 * @version 1.0
 */
public enum Facing {

    SOUTH,
    NORTH,
    WEST,
    EAST;

    /**
     * Get the opposite face
     *
     * @return opposite face
     */
    public Facing opposite() {
        switch ( this ) {
            case NORTH:
                return Facing.SOUTH;
            case SOUTH:
                return Facing.NORTH;
            case EAST:
                return Facing.WEST;
            case WEST:
                return Facing.EAST;
            default:
                return null;
        }
    }

    /**
     * Get the block face enum value for this facing value
     * @return block face value (3d) for this facing (2d)
     */
    public BlockFace toBlockFace() {
        switch ( this ) {
            case NORTH:
                return BlockFace.NORTH;
            case EAST:
                return BlockFace.EAST;
            case WEST:
                return BlockFace.WEST;
            case SOUTH:
                return BlockFace.SOUTH;
            default:
                return BlockFace.NORTH;
        }
    }

}
