package io.gomint.server.world.block;

import io.gomint.event.world.BlockPlaceEvent;
import io.gomint.inventory.item.ItemStack;
import io.gomint.math.AxisAlignedBB;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.GoMintServer;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.registry.Registry;
import io.gomint.server.world.PlacementData;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.block.generator.BlockGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Blocks {

    private static final Logger LOGGER = LoggerFactory.getLogger( Blocks.class );
    private final Registry<BlockGenerator> generators;

    /**
     * Create a new block registry
     *
     * @param server which builds this registry
     */
    public Blocks( GoMintServer server ) {
        this.generators = new Registry<>( server, clazz -> {
            try {
                // Use the same code source as the Gomint JAR
                return (BlockGenerator) ClassLoader.getSystemClassLoader().loadClass( "io.gomint.server.world.block.generator." + clazz.getSimpleName() + "Generator" ).newInstance();
            } catch ( InstantiationException | IllegalAccessException | ClassNotFoundException e ) {
                LOGGER.error( "Could not load block generator", e );
            }

            return null;
        } );

        this.generators.register( "io.gomint.server.world.block" );
    }

    public <T extends Block> T get( int blockId, byte blockData, byte skyLightLevel, byte blockLightLevel,
                                           TileEntity tileEntity, Location location, int layer ) {
        BlockGenerator instance = this.generators.getGenerator( blockId );
        if ( instance != null ) {
            if ( location == null ) {
                return instance.generate();
            }

            return instance.generate( blockId, blockData, skyLightLevel, blockLightLevel, tileEntity, location, layer );
        }

        LOGGER.warn( "Unknown block {}", blockId );
        return null;
    }

    public Block get( int blockId ) {
        BlockGenerator instance = this.generators.getGenerator( blockId );
        if ( instance != null ) {
            return instance.generate();
        }

        LOGGER.warn( "Unknown block {}", blockId );
        return null;
    }

    public <T extends Block> T get( Class<?> apiInterface ) {
        BlockGenerator instance = this.generators.getGenerator( apiInterface );
        if ( instance != null ) {
            return instance.generate();
        }

        return null;
    }

    public int getID( Class<?> block ) {
        return this.generators.getId( block );
    }

    public boolean replaceWithItem( EntityPlayer entity, Block clickedBlock, Block block, ItemStack item, Vector clickVector ) {
        // We need to change the block id first
        int id = ( (io.gomint.server.inventory.item.ItemStack) item ).getBlockId();
        BlockGenerator blockGenerator = this.generators.getGenerator( id );
        Block newBlock = blockGenerator.generate();
        if ( !newBlock.beforePlacement( entity, item, block.location ) ) {
            return false;
        }

        WorldAdapter adapter = (WorldAdapter) block.location.getWorld();

        // Check only solid blocks for bounding box intersects
        if ( newBlock.isSolid() ) {
            newBlock.setLocation( block.location ); // Temp setting, needed for getting bounding boxes
            for ( AxisAlignedBB bb : newBlock.getBoundingBox() ) {
                // Check other entities in the bounding box
                if ( adapter.getNearbyEntities( bb, null ) != null ) {
                    return false;
                }
            }
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
