package io.gomint.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface BlockLiquid extends Block {

    /**
     * Get the percentage of how high the fluid has been in this block
     *
     * @return a value between 0 and 1 where 1 is the whole block full of the given liquid
     */
    float getFillHeight();

}
