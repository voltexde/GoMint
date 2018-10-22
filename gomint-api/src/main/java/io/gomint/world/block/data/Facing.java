package io.gomint.world.block.data;

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

}
