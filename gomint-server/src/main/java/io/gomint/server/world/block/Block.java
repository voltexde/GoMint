package io.gomint.server.world.block;

import io.gomint.entity.Entity;
import io.gomint.inventory.ItemStack;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.world.WorldAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class Block implements io.gomint.world.block.Block {

    // CHECKSTYLE:OFF
    @Setter protected WorldAdapter world;
    @Setter @Getter protected Location location;
    @Setter @Getter private byte blockData;
    @Setter private TileEntity tileEntity;
    @Setter @Getter private byte skyLightLevel;
    @Setter @Getter private byte blockLightLevel;
    // CHECKSTYLE:ON

    /**
     * Called when a normal block update should be done
     *
     * @param currentTimeMS The timestamp when the tick has begun
     * @param dT            The difference time in full seconds since the last tick
     * @return a timestamp for the next execution
     */
    public long update( long currentTimeMS, float dT ) {
        return -1;
    }

    /**
     * Called when a entity decides to interact with the block
     *
     * @param entity  The entity which interacts with it
     * @param face    The block face the entity interacts with
     * @param facePos The position where the entity interacted with the block
     * @param item    The item with which the entity interacted, can be null
     */
    public void interact( Entity entity, int face, Vector facePos, ItemStack item ) {

    }

    @Override
    public boolean isTransparent() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    /**
     * Does this block need a tile entity on placement?
     *
     * @return true when it needs a tile entity, false if it doesn't
     */
    public boolean needsTileentity() {
        return false;
    }

    /**
     * Create a tile entity at the blocks location
     */
    public void createTileentity() {

    }

    /**
     * Update the data of the block and send the update
     */
    protected void updateBlock() {
        WorldAdapter worldAdapter = (WorldAdapter) this.location.getWorld();
        worldAdapter.setBlockData( this.location, this.blockData );
        worldAdapter.updateBlock( this.location );
    }

    @Override
    public <T extends io.gomint.world.block.Block> T setType( Class<T> blockType ) {
        Vector pos = this.location.toVector();
        Block instance = Blocks.get( blockType );
        if ( instance != null ) {
            WorldAdapter worldAdapter = (WorldAdapter) this.location.getWorld();
            worldAdapter.setBlockId( pos, instance.getBlockId() );
            worldAdapter.setBlockData( pos, (byte) 0 );

            // Update light
            instance.setLocation( this.location );
            WorldAdapter.getBlockLightCalculator().calculate( worldAdapter, instance );

            // Check if new block needs tile entity
            if ( instance.needsTileentity() ) {
                instance.createTileentity();
            }

            worldAdapter.updateBlock( pos );
        }

        return world.getBlockAt( pos );
    }

    /**
     * Get the break time needed to break this block without any enchantings or tools
     *
     * @return time in milliseconds it needs to break this block
     */
    public long getBreakTime() {
        return 250;
    }

    /**
     * Get the attached tile entity to this block
     *
     * @param <T>   The type of the tile entity
     * @return null when there is not tile entity attached, otherwise the stored tile entity
     */
    public <T extends TileEntity> T getTileEntity() {
        return (T) this.tileEntity;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Block ) {
            if ( ( (Block) obj ).getBlockId() == getBlockId() ) {
                return true;
            }
        }

        return false;
    }

}
