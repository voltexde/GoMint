package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.tileentity.ChestTileEntity;
import io.gomint.server.entity.tileentity.EnchantTableTileEntity;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.BlockType;

import io.gomint.server.registry.RegisterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 116 )
public class EnchantmentTable extends Block implements io.gomint.world.block.BlockEnchantmentTable {

    private static final Logger LOGGER = LoggerFactory.getLogger( EnchantmentTable.class );

    @Override
    public int getBlockId() {
        return 116;
    }

    @Override
    public long getBreakTime() {
        return 7500;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public float getBlastResistance() {
        return 6000.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.ENCHANTMENT_TABLE;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public boolean needsTileEntity() {
        return true;
    }

    @Override
    TileEntity createTileEntity( NBTTagCompound compound ) {
        return new EnchantTableTileEntity( compound, this.world );
    }

    @Override
    public boolean interact( Entity entity, int face, Vector facePos, ItemStack item ) {
        EnchantTableTileEntity tileEntity = this.getTileEntity();
        if ( tileEntity != null ) {
            tileEntity.interact( entity, face, facePos, item );
            return true;
        } else {
            LOGGER.warn( "EnchantmentTable @ " + getLocation() + " has no tile entity" );
        }

        return false;
    }

}
