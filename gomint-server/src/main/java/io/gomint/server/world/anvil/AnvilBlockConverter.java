/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

/**
 * Static class for transforming Anvil Blocks into PE blocks
 *
 * @author BlackyPaw
 * @author geNAZt
 * @version 1.1
 */
public final class AnvilBlockConverter {

	/**
     * Array holding all 256 values for blocks IDs so that basically anvil ids are merely
     * an index into these ids. For documentation concerns, please always name your IDs
     * with EOL comments with the respective block names. Also, please insert empty lines every
     * 10 IDs.
     */
    private static final byte[] CONVERSION_IDS = {
            // 0 - 9
            0,              // Air
            1,              // Stone
            2,              // Grass
            3,              // Dirt
            4,              // Cobblestone
            5,              // Wooden Planks
            6,              // Sapling
            7,              // Bedrock
            8,              // Flowing Water
            9,              // Stationary Water

            // 10 - 19
            10,             // Flowing Lava
            11,             // Stationary Lava
            12,             // Sand
            13,             // Gravel
            14,             // Gold Ore
            15,             // Iron Ore
            16,             // Coal Ore
            17,             // Log
            18,             // Leaves
            19,             // Sponge

            // 20 - 29
            20,             // Glass
            21,             // Lapis Ore
            22,             // Lapis Block
            0,              // Dispenser -> Air (maybe added in in 0.14.0?)
            24,             // Sandstone
            25,             // Note Block
            26,             // Bed
            27,             // Power Rails
            28,             // Detector Rails
            0,              // Sticky Piston -> Air (maybe added in in 0.14.0?)

            // 30 - 39
            30,             // Cobweb
            31,             // Tall Grass
            32,             // Dead Brush
            0,              // Piston -> Air (maybe added in in 0.14.0?)
            0,              // Piston Head -> Air (maybe added in in 0.14.0?)
            35,             // Wool
            0,              // Piston Extension (=block moved by piston) -> Air (maybe added in in 0.14.0?)
            37,             // Dandelion (flower)
            38,             // Rose (flower)
            39,             // Brown Mushroom

            // 40 - 49
            40,             // Red Mushroom
            41,             // Gold Block
            42,             // Iron Block
            43,             // Double Stone Slab
            44,             // Stone Slab
            45,             // Bricks
            46,             // TNT
            47,             // Bookshelf
            48,             // Moss Stone
            49,             // Obsidian

            // 50 - 59
            50,             // Torch
            51,             // Fire
            52,             // Mob Spawner
            53,             // Oak Wood Stairs
            54,             // Chest
            55,             // Redstone Wire
            56,             // Diamond Ore
            57,             // Diamond Block
            58,             // Crafting Table
            59,             // Wheat

            // 60 - 69
            60,             // Farmland
            61,             // Furnace
            62,             // Lit Furnace
            63,             // Standing Sign
            64,             // Wooden Door
            65,             // Ladder
            66,             // Rails
            67,             // Cobblestone Stairs
            68,             // Hanging Sign
            69,             // Lever

            // 70 - 79
            70,             // Stone Pressure Plate
            71,             // Iron Door
            72,             // Wooden Pressure Plate
            73,             // Redstone Ore
            74,             // Lit Redstone Ore
            75,             // Redstone Torch
            76,             // Lit Redstone Torch
            77,             // Stone Button
            78,             // Snow Layer
            79,             // Ice

            // 80 - 89
            80,             // Snow Block
            81,             // Cactus
            82,             // Clay
            83,             // Sugar Cane
            0,              // Jukebox -> Air
            85,             // Fence
            86,             // Pumpkin
            87,             // Netherrack
            88,             // Soul Sand
            89,             // Glowstone

            // 90 - 99
            90,             // Portal
            91,             // Lit Pumpkin
            92,             // Cake
            0,              // Repeater -> Air (maybe added in in 0.14.0?)
            0,              // Powered Repeater -> Air (maybe added in in 0.14.0?)
            0,              // Stained Glass -> Air (used as invisible bedrock in PE)
            96,             // Trapdoor
            97,             // Monster Egg
            98,             // Stone Brick
            99,             // Brown Mushroom Block

            // 100 - 109
            100,            // Red Mushroom Block
            101,            // Iron Bars
            102,            // Glass Pane
            103,            // Melon Block
            104,            // Pumpkin Stem
            105,            // Melon Stem
            106,            // Vines
            107,            // Fence Gate
            108,            // Brick Stairs
            109,            // Stone Brick Stairs

            // 110 - 119
            110,            // Mycelium
            111,            // Waterlily
            112,            // Nether Brick
            113,            // Nether Brick Fence
            114,            // Nether Brick Stairs
            115,            // Nether Wart
            116,            // Enchantment Table
            117,            // Brewing Stand
            0,              // Cauldron -> Air (maybe added in in 0.14.0?)
            0,              // End Portal -> Air

            // 120 - 129
            120,            // End Portal Frame
            121,            // End Stone
            0,              // Dragon Egg -> Air
            122,            // Redstone Lamp -> Redstone Lamp
            123,            // Lit Redstone Lamp -> Lit Redstone Lamp
            (byte) 157,     // Double Wooden Slab -> Double Wooden Slab
            (byte) 158,     // Wooden Slab -> Wooden Slab
            127,            // Cocoa
            (byte) 128,     // Sandstone Stairs
            (byte) 129,     // Emerald Ore

            // 130 - 139
            0,              // Ender Chest -> Air
            (byte) 131,     // Tripwire Hook
            (byte) 132,     // Tripwire
            (byte) 133,     // Emerald Block
            (byte) 134,     // Spruce Wood Stairs
            (byte) 135,     // Birch Wood Stairs
            (byte) 136,     // Jungle Wood Stairs
            0,              // Command Block -> Air (maybe added in in 0.14.0?)
            0,              // Beacon -> Air
            (byte) 139,     // Cobblestone Wall

            // 140 - 149
            (byte) 140,     // Flower Pot
            (byte) 141,     // Carrots
            (byte) 142,     // Potatoes
            (byte) 143,     // Wooden Button
            (byte) 144,     // Skull
            (byte) 145,     // Anvil
            (byte) 146,     // Trapped Chest
            (byte) 147,     // Light Weighted Pressure Plate
            (byte) 148,     // Heavy Weighted Pressure Plate
            0,              // Comparator -> Air (maybe added in in 0.14.0?)

            // 150 - 159
            0,              // Powered Comparator -> Air (maybe added in in 0.14.0?)
            (byte) 151,     // Daylight Detector
            (byte) 152,     // Redstone Block
            (byte) 153,     // Nether Quartz Ore
            0,              // Hopper -> Air
            (byte) 155,     // Quartz Block
            (byte) 156,     // Quartz Stairs
            126,            // Activator Rail -> Activator Rail
            0,              // Dropper -> Air (maybe added in in 0.14.0?)
            (byte) 159,     // Stained Clay

            // 160 - 169
            0,              // Stained Glass Pane -> Air
            (byte) 161,     // Acacia Leaves
            (byte) 162,     // Acacia Log
            (byte) 163,     // Acacia Stairs
            (byte) 164,     // Dark Oak Wood Stairs
            0,              // Slime Block -> Air
            95,             // Barrier -> Invisible Bedrock (PE only)
            (byte) 167,     // Iron Trapdoor
            0,              // Prismarine -> Air
            (byte) 169,     // Sea Lantern -> Air

            // 170 - 179
            (byte) 170,     // Hay Block
            (byte) 171,     // Carpet
            (byte) 172,     // Hardened Clay
            (byte) 173,     // Coal Block
            (byte) 174,     // Packed Ice
            (byte) 175,     // Sunflower
            0,              // Standing Banner -> Air
            0,              // Hanging Banner -> Air
            (byte) 178,     // Inverted Daylight Sensor
            24,             // Red Sandstone -> Sandstone

            // 180 - 189
            (byte) 128,     // Red Sandstone Stairs -> Sandstone Stairs
            0,              // Double Red Sandstone Slab -> Air
            0,              // Red Sandstone Slab -> Air
            (byte) 183,     // Spruce Fence Gate
            (byte) 184,     // Birch Fence Gate
            (byte) 185,     // Jungle Fence Gate
            (byte) 186,     // Dark Oak Fence Gate
            (byte) 187,     // Acacia Fence Gate
            0,              // Spruce Fence
            0,              // Birch Fence

            // 190 - 199
            0,              // Jungle Fence
            0,              // Dark Oak Fence
            0,              // Acacia Fence
            0,              // Spruce Door
            0,              // Birch Door
            0,              // Jungle Door
            0,              // Acacia Door
            0,              // Dark Oak Door
            0,              // End Rod -> Air
            0,              // Chorus Plant -> Air

            // 200 - 209
            0,              // Chorus Flower -> Air
            0,              // Purpur Block -> Air
            0,              // Purpur Pillar -> Air
            0,              // Purpur Stairs -> Air
            0,              // Double Purpur Stone Slab -> Air
            0,              // Purpur Stone Slab -> Air
            0,              // End Bricks -> Air
            0,              // Beetroots -> Air
            (byte) 198,     // Grass Path -> Grass Path
            0,              // End Gateway -> Air

            // 210 - 219
            0,              // Repeating Command Block -> Air
            0,              // Chain Command Block -> Air
            79,             // Frosted Ice -> Ice
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused

            // 220 - 229
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused

            // 230 - 239
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused

            // 240 - 249
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused

            // 250 - 255
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused
            0,              // Unused

    };

    private AnvilBlockConverter() {
        throw new AssertionError( "Cannot instantiate AnvilBlockConverter!" );
    }

    /**
     * Converts a block's ID from anvil format to its respective MCPE value.
     *
     * @param blockId The ID of the block to be converted
     * @param blockData Any additonal block data to be taken into account
     * @return The ID of the block expressed in MCPE's IDs
     */
    public static int convertBlockID( int blockId, byte blockData ) {
	    if ( blockId < 0 || blockId >= CONVERSION_IDS.length ) {
		    return 0; // Return Air for invalid block IDs
	    }
	    return CONVERSION_IDS[blockId];
    }

    /**
     * Convert's a block's additional data value from anvil format to its respective MCPE value.
     *
     * @param blockId The ID of the block whose data is to be converted
     * @param blockData The data value to be converted
     * @return The respective data value of the block expressed in MCPE's values
     */
    public static byte convertBlockData( int blockId, byte blockData ) {
	    // TODO: Implement data ID conversion:
        return blockData;
    }

}
