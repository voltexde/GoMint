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

    @Setter
    protected WorldAdapter world;
    @Setter
    @Getter
    protected Location location;
    @Setter
    @Getter
    private byte blockData;
    @Setter
    private TileEntity tileEntity;
    @Setter
    @Getter
    private byte skyLightLevel;
    @Setter
    @Getter
    private byte blockLightLevel;

    public long update( long currentTimeMS, float dT ) {
        return -1;
    }

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
            worldAdapter.updateBlock( pos );
        }

        return world.getBlockAt( pos );
    }

    public long getBreakTime() {
        return 250;
    }

    <T extends TileEntity> T getTileEntity() {
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
