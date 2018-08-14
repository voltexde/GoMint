package io.gomint.world.block;

public interface BlockStrippedAcaciaLog extends Block {

    /**
     * Set the direction of the log
     *
     * @param direction of the log
     */
    void setLogDirection( Direction direction );

    /**
     * Get the direction of this log
     *
     * @return direction of the log
     */
    Direction getLogDirection();

    enum Direction {
        UP_DOWN,
        EAST_WEST,
        NORTH_SOUTH,
        BARK
    }

}
