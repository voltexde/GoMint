package io.gomint.server.world.block;

import io.gomint.world.block.BlockType;

import io.gomint.inventory.Inventory;
import io.gomint.inventory.item.ItemStack;
import io.gomint.math.BlockPosition;
import io.gomint.math.Vector;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.tileentity.ChestTileEntity;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.BlockChest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 54 )
public class Chest extends Block implements BlockChest {

    private static final Logger LOGGER = LoggerFactory.getLogger( Chest.class );

    @Override
    public byte getBlockId() {
        return 54;
    }

    @Override
    public long getBreakTime() {
        return 3750;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean interact( Entity entity, int face, Vector facePos, ItemStack item ) {
        ChestTileEntity tileEntity = this.getTileEntity();
        if ( tileEntity != null ) {
            tileEntity.interact( entity, face, facePos, item );
        } else {
            LOGGER.warn( "Chest @ {} has no tile entity. Generating new tile entity", this.location );
            tileEntity = (ChestTileEntity) this.createTileEntity( new NBTTagCompound( "" ) );
            this.setTileEntity( tileEntity );
            this.world.storeTileEntity( this.location.toBlockPosition(), tileEntity );
            tileEntity.interact( entity, face, facePos, item );
        }

        return true;
    }

    @Override
    public Inventory getInventory() {
        ChestTileEntity tileEntity = this.getTileEntity();
        if ( tileEntity != null ) {
            return tileEntity.getInventory();
        }

        return null;
    }

    @Override
    public boolean needsTileEntity() {
        return true;
    }

    @Override
    TileEntity createTileEntity( NBTTagCompound compound ) {
        super.createTileEntity( compound );
        return new ChestTileEntity( compound, this.world );
    }

    @Override
    public float getBlastResistance() {
        return 12.5f;
    }

    @Override
    public BlockType getType() {
        return BlockType.CHEST;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

}
