/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

/**
 * @author geNAZt
 * @version 1.0
 *
 * Static class for transforming Anvil Blocks into PE blocks
 */
public class AnvilBlockConverter {
    /**
     * Check if the given BlockID needs to be converted
     *
     * @param blockId which we need to check
     * @param data which the block has
     * @return true when it needs to be converted, false if not
     */
    public static boolean needsToConvert( int blockId, byte data ) {
        switch ( blockId ) {
            case 126: // Wood Slab on PC -> Activator Rail on PE
                return true;
        }

        return false;
    }

    /**
     * Get the converted BlockID for PE
     *
     * @param blockId which should be converted from PC
     * @param blockData which should be converted from PC
     * @return new block id which can be used in PE
     */
    public static int convertBlockID( int blockId, byte blockData ) {
        switch ( blockId ) {
            case 126: // Wood Slab on PC -> Activator Rail on PE
                return 158;
        }

        return blockId;
    }

    /**
     * Convert the data of a Block
     *
     * @param blockId which should be converted from PC
     * @param blockData which should be converted from PC
     * @return new data value for PE
     */
    public static byte convertBlockData( int blockId, byte blockData ) {
        return blockData;
    }
}
