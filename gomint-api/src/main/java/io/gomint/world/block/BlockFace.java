package io.gomint.world.block;

import io.gomint.world.block.data.Facing;

/**
 * @author geNAZt
 * @version 1.0
 */
public enum BlockFace {

    // CHECKSTYLE:OFF
    DOWN,
    UP,
    EAST,
    WEST,
    NORTH,
    SOUTH;
    // CHECKSTYLE:ON

    /**
     * Get the opposite of the current facing
     *
     * @return opposite facing site
     */
    public BlockFace opposite() {
        switch ( this ) {
            case DOWN:
                return UP;
            case UP:
                return DOWN;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
        }

        return NORTH;
    }

    /**
     * Get the face enum value for this block facing value
     * @return face value (2d) for this block facing (3d)
     */
    public Facing toFacing() {
        switch ( this ) {
            case NORTH:
                return Facing.NORTH;
            case EAST:
                return Facing.EAST;
            case WEST:
                return Facing.WEST;
            case SOUTH:
                return Facing.SOUTH;
            default:
                return Facing.NORTH;
        }
    }

}
