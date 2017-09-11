package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.registry.GeneratorCallback;
import io.gomint.server.registry.Registry;
import io.gomint.server.world.block.generator.BlockGenerator;
import javassist.*;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Blocks {

    private static final Registry<BlockGenerator> GENERATORS = new Registry<>( new GeneratorCallback<BlockGenerator>() {
        @Override
        public BlockGenerator generate( int id, Class<?> clazz ) {
            // Create generated Generator for this block
            ClassPool pool = ClassPool.getDefault();
            CtClass generatorCT = pool.makeClass( "io.gomint.server.world.block.generator." + clazz.getSimpleName() );

            try {
                generatorCT.setInterfaces( new CtClass[]{ pool.get( "io.gomint.server.world.block.generator.BlockGenerator" ) } );
            } catch ( NotFoundException e ) {
                e.printStackTrace();
                return null;
            }

            try {
                generatorCT.addMethod( CtNewMethod.make( "public Object generate( byte blockData, byte skyLightLevel, byte blockLightLevel, io.gomint.server.entity.tileentity.TileEntity tileEntity, io.gomint.math.Location location ) {" +
                        "io.gomint.server.world.block.Block block = new " + clazz.getName() + "();" +
                        "            block.setBlockData( blockData );\n" +
                        "            block.setTileEntity( tileEntity );\n" +
                        "            block.setWorld( (io.gomint.server.world.WorldAdapter) location.getWorld() );\n" +
                        "            block.setLocation( location );\n" +
                        "            block.setSkyLightLevel( skyLightLevel );\n" +
                        "            block.setBlockLightLevel( blockLightLevel );" +
                        "return block;" +
                        "}", generatorCT ) );

                generatorCT.addMethod( CtNewMethod.make( "public Object generate() { return new " + clazz.getName() + "(); }", generatorCT ) );
            } catch ( CannotCompileException e ) {
                e.printStackTrace();
                return null;
            }

            try {
                return (BlockGenerator) generatorCT.toClass().newInstance();
            } catch ( InstantiationException | IllegalAccessException | CannotCompileException e ) {
                e.printStackTrace();
            }

            return null;
        }
    } );

    /*private static final BlockGenerator[] KNOWN_BLOCKS = new BlockGenerator[256];
    private static final ObjIntMap<Class<?>> KNOWN_API_INTERFACES = HashObjIntMaps.newMutableMap();

    public static final Air AIR = createBlock( 0, Air.class );
    public static final Stone STONE = createBlock( 1, Stone.class );
    public static final GrassBlock GRASS_BLOCK = createBlock( 2, GrassBlock.class );
    public static final Dirt DIRT = createBlock( 3, Dirt.class );
    public static final Cobblestone COBBLESTONE = createBlock( 4, Cobblestone.class );
    public static final WoodPlanks WOOD_PLANKS = createBlock( 5, WoodPlanks.class );
    public static final Sapling SAPLING = createBlock( 6, Sapling.class );
    public static final Bedrock BEDROCK = createBlock( 7, Bedrock.class );
    public static final Water WATER = createBlock( 8, Water.class );
    public static final StationaryWater STATIONARY_WATER = createBlock( 9, StationaryWater.class );
    public static final Lava LAVA = createBlock( 10, Lava.class );
    public static final StationaryLava STATIONARY_LAVA = createBlock( 11, StationaryLava.class );
    public static final Sand SAND = createBlock( 12, Sand.class );
    public static final Gravel GRAVEL = createBlock( 13, Gravel.class );
    public static final GoldOre GOLD_ORE = createBlock( 14, GoldOre.class );
    public static final IronOre IRON_ORE = createBlock( 15, IronOre.class );
    public static final CoalOre COAL_ORE = createBlock( 16, CoalOre.class );
    public static final Wood WOOD = createBlock( 17, Wood.class );
    public static final Leaves LEAVES = createBlock( 18, Leaves.class );
    public static final Sponge SPONGE = createBlock( 19, Sponge.class );
    public static final Glass GLASS = createBlock( 20, Glass.class );
    public static final LapisLazuliOre LAPIS_LAZULI_ORE = createBlock( 21, LapisLazuliOre.class );
    public static final LapisLazuliBlock LAPIS_LAZULI_BLOCK = createBlock( 22, LapisLazuliBlock.class );
    public static final Dispenser DISPENSER = createBlock( 23, Dispenser.class );
    public static final Sandstone SANDSTONE = createBlock( 24, Sandstone.class );
    public static final NoteBlock NOTE_BLOCK = createBlock( 25, NoteBlock.class );
    public static final Bed BED = createBlock( 26, Bed.class );
    public static final PoweredRail POWERED_RAIL = createBlock( 27, PoweredRail.class );
    public static final DetectorRail DETECTOR_RAIL = createBlock( 28, DetectorRail.class );
    public static final StickyPiston STICKY_PISTON = createBlock( 29, StickyPiston.class );
    public static final Cobweb COBWEB = createBlock( 30, Cobweb.class );
    public static final TallGrass TALL_GRASS = createBlock( 31, TallGrass.class );
    public static final DeadBush DEAD_BUSH = createBlock( 32, DeadBush.class );
    public static final Piston PISTON = createBlock( 33, Piston.class );
    public static final PistonHead PISTON_HEAD = createBlock( 34, PistonHead.class );
    public static final Wool WOOL = createBlock( 35, Wool.class );
    public static final Dandelion DANDELION = createBlock( 37, Dandelion.class );
    public static final Flower FLOWER = createBlock( 38, Flower.class );
    public static final BrownMushroom BROWN_MUSHROOM = createBlock( 39, BrownMushroom.class );
    public static final RedMushroom RED_MUSHROOM = createBlock( 40, RedMushroom.class );
    public static final BlockOfGold BLOCK_OF_GOLD = createBlock( 41, BlockOfGold.class );
    public static final BlockOfIron BLOCK_OF_IRON = createBlock( 42, BlockOfIron.class );
    public static final DoubleStoneSlab DOUBLE_STONE_SLAB = createBlock( 43, DoubleStoneSlab.class );
    public static final StoneSlab STONE_SLAB = createBlock( 44, StoneSlab.class );
    public static final Bricks BRICKS = createBlock( 45, Bricks.class );
    public static final TNT TNT = createBlock( 46, TNT.class );
    public static final Bookshelf BOOKSHELF = createBlock( 47, Bookshelf.class );
    public static final MossStone MOSS_STONE = createBlock( 48, MossStone.class );
    public static final Obsidian OBSIDIAN = createBlock( 49, Obsidian.class );
    public static final Torch TORCH = createBlock( 50, Torch.class );
    public static final Fire FIRE = createBlock( 51, Fire.class );
    public static final MonsterSpawner MONSTER_SPAWNER = createBlock( 52, MonsterSpawner.class );
    public static final OakWoodStairs OAK_WOOD_STAIRS = createBlock( 53, OakWoodStairs.class );
    public static final Chest CHEST = createBlock( 54, Chest.class );
    public static final RedstoneWire REDSTONE_WIRE = createBlock( 55, RedstoneWire.class );
    public static final DiamondOre DIAMOND_ORE = createBlock( 56, DiamondOre.class );
    public static final BlockOfDiamond BLOCK_OF_DIAMOND = createBlock( 57, BlockOfDiamond.class );
    public static final CraftingTable CRAFTING_TABLE = createBlock( 58, CraftingTable.class );
    public static final Crops CROPS = createBlock( 59, Crops.class );
    public static final Farmland FARMLAND = createBlock( 60, Farmland.class );
    public static final Furnace FURNACE = createBlock( 61, Furnace.class );
    public static final BurningFurnace BURNING_FURNACE = createBlock( 62, BurningFurnace.class );
    public static final Sign SIGN = createBlock( 63, Sign.class );
    public static final WoodenDoor WOODEN_DOOR = createBlock( 64, WoodenDoor.class );
    public static final Ladder LADDER = createBlock( 65, Ladder.class );
    public static final Rail RAIL = createBlock( 66, Rail.class );
    public static final CobblestoneStairs COBBLESTONE_STAIRS = createBlock( 67, CobblestoneStairs.class );
    public static final WallSign WALL_SIGN = createBlock( 68, WallSign.class );
    public static final Lever LEVER = createBlock( 69, Lever.class );
    public static final StonePressurePlate STONE_PRESSURE_PLATE = createBlock( 70, StonePressurePlate.class );
    public static final IronDoor IRON_DOOR = createBlock( 71, IronDoor.class );
    public static final WoodenPressurePlate WOODEN_PRESSURE_PLATE = createBlock( 72, WoodenPressurePlate.class );
    public static final RedstoneOre REDSTONE_ORE = createBlock( 73, RedstoneOre.class );
    public static final GlowingRedstoneOre GLOWING_REDSTONE_ORE = createBlock( 74, GlowingRedstoneOre.class );
    public static final RedstoneTorchInactive REDSTONE_TORCH_INACTIVE = createBlock( 75, RedstoneTorchInactive.class );
    public static final RedstoneTorchActive REDSTONE_TORCH_ACTIVE = createBlock( 76, RedstoneTorchActive.class );
    public static final StoneButton STONE_BUTTON = createBlock( 77, StoneButton.class );
    public static final TopSnow TOP_SNOW = createBlock( 78, TopSnow.class );
    public static final Ice ICE = createBlock( 79, Ice.class );
    public static final Snow SNOW = createBlock( 80, Snow.class );
    public static final Cactus CACTUS = createBlock( 81, Cactus.class );
    public static final Clay CLAY = createBlock( 82, Clay.class );
    public static final SugarCane SUGAR_CANE = createBlock( 83, SugarCane.class );
    public static final Fence FENCE = createBlock( 85, Fence.class );
    public static final Pumpkin PUMPKIN = createBlock( 86, Pumpkin.class );
    public static final Netherrack NETHERRACK = createBlock( 87, Netherrack.class );
    public static final SoulSand SOUL_SAND = createBlock( 88, SoulSand.class );
    public static final Glowstone GLOWSTONE = createBlock( 89, Glowstone.class );
    public static final Portal PORTAL = createBlock( 90, Portal.class );
    public static final JackOLantern JACK_O_LANTERN = createBlock( 91, JackOLantern.class );
    public static final Cake CAKE = createBlock( 92, Cake.class );
    public static final RedstoneRepeaterInactive REDSTONE_REPEATER_INACTIVE = createBlock( 93, RedstoneRepeaterInactive.class );
    public static final RedstoneRepeaterActive REDSTONE_REPEATER_ACTIVE = createBlock( 94, RedstoneRepeaterActive.class );
    public static final InvisibleBedrock INVISIBLE_BEDROCK = createBlock( 95, InvisibleBedrock.class );
    public static final Trapdoor TRAPDOOR = createBlock( 96, Trapdoor.class );
    public static final MonsterEgg MONSTER_EGG = createBlock( 97, MonsterEgg.class );
    public static final StoneBrick STONE_BRICK = createBlock( 98, StoneBrick.class );
    public static final BrownMushroomBlock BROWN_MUSHROOM_BLOCK = createBlock( 99, BrownMushroomBlock.class );
    public static final RedMushroomBlock RED_MUSHROOM_BLOCK = createBlock( 100, RedMushroomBlock.class );
    public static final IronBars IRON_BARS = createBlock( 101, IronBars.class );
    public static final GlassPane GLASS_PANE = createBlock( 102, GlassPane.class );
    public static final Melon MELON = createBlock( 103, Melon.class );
    public static final PumpkinStem PUMPKIN_STEM = createBlock( 104, PumpkinStem.class );
    public static final MelonStem MELON_STEM = createBlock( 105, MelonStem.class );
    public static final Vines VINES = createBlock( 106, Vines.class );
    public static final FenceGate FENCE_GATE = createBlock( 107, FenceGate.class );
    public static final BrickStairs BRICK_STAIRS = createBlock( 108, BrickStairs.class );
    public static final StoneBrickStairs STONE_BRICK_STAIRS = createBlock( 109, StoneBrickStairs.class );
    public static final Mycelium MYCELIUM = createBlock( 110, Mycelium.class );
    public static final LilyPad LILY_PAD = createBlock( 111, LilyPad.class );
    public static final NetherBrick NETHER_BRICK = createBlock( 112, NetherBrick.class );
    public static final NetherBrickFence NETHER_BRICK_FENCE = createBlock( 113, NetherBrickFence.class );
    public static final NetherBrickStairs NETHER_BRICK_STAIRS = createBlock( 114, NetherBrickStairs.class );
    public static final NetherWart NETHER_WART = createBlock( 115, NetherWart.class );
    public static final EnchantmentTable ENCHANTMENT_TABLE = createBlock( 116, EnchantmentTable.class );
    public static final BrewingStand BREWING_STAND = createBlock( 117, BrewingStand.class );
    public static final Cauldron CAULDRON = createBlock( 118, Cauldron.class );
    public static final EndPortal END_PORTAL = createBlock( 119, EndPortal.class );
    public static final EndPortalFrame END_PORTAL_FRAME = createBlock( 120, EndPortalFrame.class );
    public static final EndStone END_STONE = createBlock( 121, EndStone.class );
    public static final DragonEgg DRAGON_EGG = createBlock( 122, DragonEgg.class );
    public static final RedstoneLampInactive REDSTONE_LAMP_INACTIVE = createBlock( 123, RedstoneLampInactive.class );
    public static final RedstoneLampActive REDSTONE_LAMP_ACTIVE = createBlock( 124, RedstoneLampActive.class );
    public static final Dropper DROPPER = createBlock( 125, Dropper.class );
    public static final ActivatorRail ACTIVATOR_RAIL = createBlock( 126, ActivatorRail.class );
    public static final Cocoa COCOA = createBlock( 127, Cocoa.class );
    public static final SandstoneStairs SANDSTONE_STAIRS = createBlock( 128, SandstoneStairs.class );
    public static final EmeraldOre EMERALD_ORE = createBlock( 129, EmeraldOre.class );
    public static final EnderChest ENDER_CHEST = createBlock( 130, EnderChest.class );
    public static final TripwireHook TRIPWIRE_HOOK = createBlock( 131, TripwireHook.class );
    public static final Tripwire TRIPWIRE = createBlock( 132, Tripwire.class );
    public static final BlockOfEmerald BLOCK_OF_EMERALD = createBlock( 133, BlockOfEmerald.class );
    public static final SpruceWoodStairs SPRUCE_WOOD_STAIRS = createBlock( 134, SpruceWoodStairs.class );
    public static final BirchWoodStairs BIRCH_WOOD_STAIRS = createBlock( 135, BirchWoodStairs.class );
    public static final JungleWoodStairs JUNGLE_WOOD_STAIRS = createBlock( 136, JungleWoodStairs.class );
    public static final Beacon BEACON = createBlock( 138, Beacon.class );
    public static final CobblestoneWall COBBLESTONE_WALL = createBlock( 139, CobblestoneWall.class );
    public static final FlowerPot FLOWER_POT = createBlock( 140, FlowerPot.class );
    public static final Carrots CARROTS = createBlock( 141, Carrots.class );
    public static final Potato POTATO = createBlock( 142, Potato.class );
    public static final WoodenButton WOODEN_BUTTON = createBlock( 143, WoodenButton.class );
    public static final MobHead MOB_HEAD = createBlock( 144, MobHead.class );
    public static final Anvil ANVIL = createBlock( 145, Anvil.class );
    public static final TrappedChest TRAPPED_CHEST = createBlock( 146, TrappedChest.class );
    public static final WeightedPressurePlateLight WEIGHTED_PRESSURE_PLATE_LIGHT = createBlock( 147, WeightedPressurePlateLight.class );
    public static final WeightedPressurePlateHeavy WEIGHTED_PRESSURE_PLATE_HEAVY = createBlock( 148, WeightedPressurePlateHeavy.class );
    public static final RedstoneComparatorUnpowered REDSTONE_COMPARATOR_UNPOWERED = createBlock( 149, RedstoneComparatorUnpowered.class );
    public static final RedstoneComparatorPowered REDSTONE_COMPARATOR_POWERED = createBlock( 150, RedstoneComparatorPowered.class );
    public static final DaylightSensor DAYLIGHT_SENSOR = createBlock( 151, DaylightSensor.class );
    public static final BlockOfRedstone BLOCK_OF_REDSTONE = createBlock( 152, BlockOfRedstone.class );
    public static final NetherQuartzOre NETHER_QUARTZ_ORE = createBlock( 153, NetherQuartzOre.class );
    public static final Hopper HOPPER = createBlock( 154, Hopper.class );
    public static final BlockOfQuartz BLOCK_OF_QUARTZ = createBlock( 155, BlockOfQuartz.class );
    public static final QuartzStairs QUARTZ_STAIRS = createBlock( 156, QuartzStairs.class );
    public static final WoodenDoubleSlab WOODEN_DOUBLE_SLAB = createBlock( 157, WoodenDoubleSlab.class );
    public static final WoodenSlab WOODEN_SLAB = createBlock( 158, WoodenSlab.class );
    public static final StainedClay STAINED_CLAY = createBlock( 159, StainedClay.class );
    public static final StainedGlassPane STAINED_GLASS_PANE = createBlock( 160, StainedGlassPane.class );
    public static final AcaciaLeaves ACACIA_LEAVES = createBlock( 161, AcaciaLeaves.class );
    public static final AcaciaWood ACACIA_WOOD = createBlock( 162, AcaciaWood.class );
    public static final AcaciaWoodStairs ACACIA_WOOD_STAIRS = createBlock( 163, AcaciaWoodStairs.class );
    public static final DarkOakWoodStairs DARK_OAK_WOOD_STAIRS = createBlock( 164, DarkOakWoodStairs.class );
    public static final SlimeBlock SLIME_BLOCK = createBlock( 165, SlimeBlock.class );
    public static final IronTrapdoor IRON_TRAPDOOR = createBlock( 167, IronTrapdoor.class );
    public static final Prismarine PRISMARINE = createBlock( 168, Prismarine.class );
    public static final SeaLantern SEA_LANTERN = createBlock( 169, SeaLantern.class );
    public static final HayBale HAY_BALE = createBlock( 170, HayBale.class );
    public static final Carpet CARPET = createBlock( 171, Carpet.class );
    public static final HardenedClay HARDENED_CLAY = createBlock( 172, HardenedClay.class );
    public static final BlockOfCoal BLOCK_OF_COAL = createBlock( 173, BlockOfCoal.class );
    public static final PackedIce PACKED_ICE = createBlock( 174, PackedIce.class );
    public static final Sunflower SUNFLOWER = createBlock( 175, Sunflower.class );
    public static final InvertedDaylightSensor INVERTED_DAYLIGHT_SENSOR = createBlock( 178, InvertedDaylightSensor.class );
    public static final RedSandstone RED_SANDSTONE = createBlock( 179, RedSandstone.class );
    public static final RedSandstoneStairs RED_SANDSTONE_STAIRS = createBlock( 180, RedSandstoneStairs.class );
    public static final DoubleRedSandstoneSlab DOUBLE_RED_SANDSTONE_SLAB = createBlock( 181, DoubleRedSandstoneSlab.class );
    public static final RedSandstoneSlab RED_SANDSTONE_SLAB = createBlock( 182, RedSandstoneSlab.class );
    public static final SpruceFenceGate SPRUCE_FENCE_GATE = createBlock( 183, SpruceFenceGate.class );
    public static final BirchFenceGate BIRCH_FENCE_GATE = createBlock( 184, BirchFenceGate.class );
    public static final JungleFenceGate JUNGLE_FENCE_GATE = createBlock( 185, JungleFenceGate.class );
    public static final DarkOakFenceGate DARK_OAK_FENCE_GATE = createBlock( 186, DarkOakFenceGate.class );
    public static final AcaciaFenceGate ACACIA_FENCE_GATE = createBlock( 187, AcaciaFenceGate.class );
    public static final SpruceDoor SPRUCE_DOOR = createBlock( 193, SpruceDoor.class );
    public static final BirchDoor BIRCH_DOOR = createBlock( 194, BirchDoor.class );
    public static final JungleDoor JUNGLE_DOOR = createBlock( 195, JungleDoor.class );
    public static final AcaciaDoor ACACIA_DOOR = createBlock( 196, AcaciaDoor.class );
    public static final DarkOakDoor DARK_OAK_DOOR = createBlock( 197, DarkOakDoor.class );
    public static final GrassPath GRASS_PATH = createBlock( 198, GrassPath.class );
    public static final ItemFrame ITEM_FRAME = createBlock( 199, ItemFrame.class );
    public static final ChorusFlower CHORUS_FLOWER = createBlock( 200, ChorusFlower.class );
    public static final PurpurBlock PURPUR_BLOCK = createBlock( 201, PurpurBlock.class );
    public static final PurpurStairs PURPUR_STAIRS = createBlock( 203, PurpurStairs.class );
    public static final EndBricks END_BRICKS = createBlock( 206, EndBricks.class );
    public static final EndRod END_ROD = createBlock( 208, EndRod.class );
    public static final EndGateway END_GATEWAY = createBlock( 209, EndGateway.class );
    public static final ChorusPlant CHORUS_PLANT = createBlock( 240, ChorusPlant.class );
    public static final StainedGlass STAINED_GLASS = createBlock( 241, StainedGlass.class );
    public static final Podzol PODZOL = createBlock( 243, Podzol.class );
    public static final Beetroot BEETROOT = createBlock( 244, Beetroot.class );
    public static final Stonecutter STONECUTTER = createBlock( 245, Stonecutter.class );
    public static final GlowingObsidian GLOWING_OBSIDIAN = createBlock( 246, GlowingObsidian.class );
    public static final NetherReactorCore NETHER_REACTOR_CORE = createBlock( 247, NetherReactorCore.class );
    public static final UpdateGameBlockUpdate1 UPDATE_GAME_BLOCK_1 = createBlock( 248, UpdateGameBlockUpdate1.class );
    public static final UpdateGameBlockUpdate2 UPDATE_GAME_BLOCK_2 = createBlock( 249, UpdateGameBlockUpdate2.class );
    public static final BlockMovedByPiston BLOCK_MOVED_BY_PISTON = createBlock( 250, BlockMovedByPiston.class );
    public static final Observer OBSERVER = createBlock( 251, Observer.class );
    public static final Reserved6 RESERVED6 = createBlock( 255, Reserved6.class );
    public static final Allow ALLOW = createBlock( 210, Allow.class );
    public static final Deny DENY = createBlock( 211, Deny.class );
    public static final BorderBlock BORDER_BLOCK = createBlock( 212, BorderBlock.class );
    public static final Chalkboard CHALKBOARD = createBlock( 230, Chalkboard.class );
    public static final Camera CAMERA = createBlock( 242, Camera.class );

    private static <T extends Block> T createBlock( int blockId, Class<T> blockClass ) {
        // Create generated Generator for this block
        ClassPool pool = ClassPool.getDefault();
        CtClass generatorCT = pool.makeClass( "io.gomint.server.world.block.generator." + blockClass.getSimpleName() );

        try {
            generatorCT.setInterfaces( new CtClass[]{ pool.get( "io.gomint.server.world.block.generator.BlockGenerator" ) } );
        } catch ( NotFoundException e ) {
            e.printStackTrace();
        }

        try {
            generatorCT.addMethod( CtNewMethod.make( "public Object generate( byte blockData, byte skyLightLevel, byte blockLightLevel, io.gomint.server.entity.tileentity.TileEntity tileEntity, io.gomint.math.Location location ) {" +
                    "io.gomint.server.world.block.Block block = new " + blockClass.getName() + "();" +
                    "            block.setBlockData( blockData );\n" +
                    "            block.setTileEntity( tileEntity );\n" +
                    "            block.setWorld( (io.gomint.server.world.WorldAdapter) location.getWorld() );\n" +
                    "            block.setLocation( location );\n" +
                    "            block.setSkyLightLevel( skyLightLevel );\n" +
                    "            block.setBlockLightLevel( blockLightLevel );" +
                    "return block;" +
                    "}", generatorCT ) );

            generatorCT.addMethod( CtNewMethod.make( "public Object generate() { return new " + blockClass.getName() + "(); }", generatorCT ) );
        } catch ( CannotCompileException e ) {
            e.printStackTrace();
        }

        try {
            T instance = blockClass.newInstance();
            KNOWN_BLOCKS[blockId] = (BlockGenerator) generatorCT.toClass().newInstance();

            // Check for API interface
            for ( Class<?> aClass : instance.getClass().getInterfaces() ) {
                KNOWN_API_INTERFACES.put( aClass, blockId );
            }

            // For ease of use also put the class into the api interfaces
            KNOWN_API_INTERFACES.put( blockClass, blockId );

            return instance;
        } catch ( IllegalAccessException | InstantiationException e ) {
            e.printStackTrace();
            System.exit( -2 );
        } catch ( CannotCompileException e ) {
            e.printStackTrace();
        }

        return null;
    }*/

    public static <T extends Block> T get( int blockId, byte blockData, byte skyLightLevel, byte blockLightLevel,
                                           TileEntity tileEntity, Location location ) {
        BlockGenerator instance = GENERATORS.getGenerator( blockId );
        if ( instance != null ) {
            return instance.generate( blockData, skyLightLevel, blockLightLevel, tileEntity, location );
        }

        return null;
    }

    public static <T extends Block> T get( Class<?> apiInterface ) {
        BlockGenerator instance = GENERATORS.getGenerator( apiInterface );
        if ( instance != null ) {
            return instance.generate();
        }

        return null;
    }

    public static boolean replaceWithItem( Entity entity, Block block, ItemStack item, Vector clickVector ) {
        // We need to change the block id first
        int id = ( (io.gomint.server.inventory.item.ItemStack) item ).getBlockId();
        BlockGenerator blockGenerator = GENERATORS.getGenerator( id );
        Block newBlock = blockGenerator.generate();
        if ( !newBlock.beforePlacement( item, block.location ) ) {
            return false;
        }

        byte data = newBlock.calculatePlacementData( entity, item, clickVector );
        block = block.setType( newBlock.getClass(), data );
        block.afterPlacement();
        return true;
    }

}
