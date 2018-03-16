package io.gomint.server.world.block;

import io.gomint.event.world.BlockPlaceEvent;
import io.gomint.inventory.item.ItemStack;
import io.gomint.math.AxisAlignedBB;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.registry.Registry;
import io.gomint.server.world.PlacementData;
import io.gomint.server.world.block.generator.BlockGenerator;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Blocks {

    private static final Logger LOGGER = LoggerFactory.getLogger( Blocks.class );
    private static final Registry<BlockGenerator> GENERATORS = new Registry<>( clazz -> {
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
            generatorCT.addMethod( CtNewMethod.make( "public io.gomint.server.world.block.Block generate( byte blockId, byte blockData, byte skyLightLevel, byte blockLightLevel, io.gomint.server.entity.tileentity.TileEntity tileEntity, io.gomint.math.Location location ) {" +
                "io.gomint.server.world.block.Block block = new " + clazz.getName() + "();" +
                "block.setData( blockId, blockData, tileEntity, (io.gomint.server.world.WorldAdapter) location.getWorld(), location, skyLightLevel, blockLightLevel );\n" +
                "return block;" +
                "}", generatorCT ) );

            generatorCT.addMethod( CtNewMethod.make( "public io.gomint.server.world.block.Block generate() { return new " + clazz.getName() + "(); }", generatorCT ) );
        } catch ( CannotCompileException e ) {
            e.printStackTrace();
            return null;
        }

        try {
            // Use the same code source as the Gomint JAR
            return (BlockGenerator) generatorCT.toClass( ClassLoader.getSystemClassLoader(), null ).newInstance();
        } catch ( InstantiationException | IllegalAccessException | CannotCompileException e ) {
            e.printStackTrace();
        }

        return null;
    } );

    static {
        GENERATORS.register( "io.gomint.server.world.block" );
    }

    public static <T extends Block> T get( int blockId, byte blockData, byte skyLightLevel, byte blockLightLevel,
                                           TileEntity tileEntity, Location location ) {
        BlockGenerator instance = GENERATORS.getGenerator( blockId );
        if ( instance != null ) {
            if ( location == null ) {
                return instance.generate();
            }

            return instance.generate( (byte) blockId, blockData, skyLightLevel, blockLightLevel, tileEntity, location );
        }

        LOGGER.warn( "Unknown block {}", blockId );
        return null;
    }

    public static Block get( byte blockId ) {
        BlockGenerator instance = GENERATORS.getGenerator( blockId & 0xFF );
        if ( instance != null ) {
            return instance.generate();
        }

        LOGGER.warn( "Unknown block {}", blockId );
        return null;
    }

    public static <T extends Block> T get( Class<?> apiInterface ) {
        BlockGenerator instance = GENERATORS.getGenerator( apiInterface );
        if ( instance != null ) {
            return instance.generate();
        }

        return null;
    }

    public static int getID( Class<?> block ) {
        return GENERATORS.getId( block );
    }

    public static boolean replaceWithItem( EntityPlayer entity, Block clickedBlock, Block block, ItemStack item, Vector clickVector ) {
        // We need to change the block id first
        int id = ( (io.gomint.server.inventory.item.ItemStack) item ).getBlockId();
        BlockGenerator blockGenerator = GENERATORS.getGenerator( id );
        Block newBlock = blockGenerator.generate();
        if ( !newBlock.beforePlacement( entity, item, block.location ) ) {
            return false;
        }

        // Check if we want to replace the block we are in
        AxisAlignedBB tempBoundingBox = new AxisAlignedBB( block.location.getX(), block.location.getY(), block.location.getZ(),
            block.location.getX() + 1, block.location.getY() + 1, block.location.getZ() + 1 );
        if ( entity.getBoundingBox().intersectsWith( tempBoundingBox ) ) {
            return false;
        }

        // We decided that the block would fit
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent( entity, clickedBlock, block, item, newBlock );
        block.world.getServer().getPluginManager().callEvent( blockPlaceEvent );

        if ( blockPlaceEvent.isCancelled() ) {
            return false;
        }

        PlacementData data = newBlock.calculatePlacementData( entity, item, clickVector );
        block = block.setBlockFromPlacementData( data );
        block.afterPlacement( data );
        return true;
    }

}
