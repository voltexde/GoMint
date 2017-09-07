package io.gomint.server.world.block;

import io.gomint.entity.Entity;
import io.gomint.inventory.ItemStack;
import io.gomint.math.Vector;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class Door extends Block implements io.gomint.world.block.Door {

    @Override
    public boolean isTop() {
        return ( getBlockData() & 0x08 ) == 0x08;
    }

    @Override
    public boolean isOpen() {
        if ( isTop() ) {
            Door otherPart = getLocation().getWorld().getBlockAt( getLocation().add( 0, -1, 0 ) );
            return otherPart.isOpen();
        }

        return ( getBlockData() & 0x04 ) == 0x04;
    }

    @Override
    public void toggle() {
        if ( isTop() ) {
            Door otherPart = getLocation().getWorld().getBlockAt( getLocation().add( 0, -1, 0 ) );
            otherPart.toggle();
            return;
        }

        setBlockData( (byte) ( getBlockData() ^ 0x04 ) );
        updateBlock();
    }

    @Override
    public boolean interact( Entity entity, int face, Vector facePos, ItemStack item ) {
        // Open / Close the door
        // TODO: Door events
        toggle();

        return true;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public long getBreakTime() {
        return 4500;
    }

}
