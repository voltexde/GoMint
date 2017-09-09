package io.gomint.math;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author geNAZt
 */
@AllArgsConstructor
@Data
public class BlockPosition {

    public static final BlockPosition UP = new BlockPosition( 0, 1, 0 );
    public static final BlockPosition DOWN = new BlockPosition( 0, -1, 0 );

    public static final BlockPosition EAST = new BlockPosition( 1, 0, 0 );
    public static final BlockPosition WEST = new BlockPosition( -1, 0, 0 );
    public static final BlockPosition NORTH = new BlockPosition( 0, 0, -1 );
    public static final BlockPosition SOUTH = new BlockPosition( 0, 0, 1 );

    private int x, y, z;

    public Vector toVector() {
        return new Vector( this.x, this.y, this.z );
    }

    public BlockPosition add( BlockPosition other ) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }
}
