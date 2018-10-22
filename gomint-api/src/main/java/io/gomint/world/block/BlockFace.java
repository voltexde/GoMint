package io.gomint.world.block;

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

}
