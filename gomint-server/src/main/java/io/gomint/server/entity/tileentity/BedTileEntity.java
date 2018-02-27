package io.gomint.server.entity.tileentity;

import io.gomint.math.Location;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.block.data.BlockColor;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BedTileEntity extends TileEntity {

    private BlockColor color;

    public BedTileEntity( BlockColor color, Location location ) {
        super( location );

        this.color = color;
    }

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    public BedTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );

        this.detectColor( tagCompound.getByte( "color", (byte) 0 ) );
    }

    /**
     * Select the correct color based on the byte representation inside the compound
     *
     * @param color value which should be detected
     */
    public void detectColor( byte color ) {
        switch ( color ) {
            case 0:
                this.color = BlockColor.WHITE;
                break;
            case 1:
                this.color = BlockColor.ORANGE;
                break;
            case 2:
                this.color = BlockColor.MAGENTA;
                break;
            case 3:
                this.color = BlockColor.LIGHT_BLUE;
                break;
            case 4:
                this.color = BlockColor.YELLOW;
                break;
            case 5:
                this.color = BlockColor.LIME;
                break;
            case 6:
                this.color = BlockColor.PINK;
                break;
            case 7:
                this.color = BlockColor.GRAY;
                break;
            case 8:
                this.color = BlockColor.LIGHT_GRAY;
                break;
            case 9:
                this.color = BlockColor.CYAN;
                break;
            case 10:
                this.color = BlockColor.PURPLE;
                break;
            case 11:
                this.color = BlockColor.BLUE;
                break;
            case 12:
                this.color = BlockColor.BROWN;
                break;
            case 13:
                this.color = BlockColor.GREEN;
                break;
            case 14:
                this.color = BlockColor.RED;
                break;
            default:
                this.color = BlockColor.BLACK;
        }
    }

    public BlockColor getColor() {
        return this.color;
    }

    public void setColor( BlockColor color ) {
        this.color = color;
    }

    @Override
    public void toCompound( NBTTagCompound compound ) {
        super.toCompound( compound );

        byte color = 0;
        switch ( this.color ) {
            case WHITE:
                color = 0;
                break;
            case ORANGE:
                color = 1;
                break;
            case MAGENTA:
                color = 2;
                break;
            case LIGHT_BLUE:
                color = 3;
                break;
            case YELLOW:
                color = 4;
                break;
            case LIME:
                color = 5;
                break;
            case PINK:
                color = 6;
                break;
            case GRAY:
                color = 7;
                break;
            case LIGHT_GRAY:
                color = 8;
                break;
            case CYAN:
                color = 9;
                break;
            case PURPLE:
                color = 10;
                break;
            case BLUE:
                color = 11;
                break;
            case BROWN:
                color = 12;
                break;
            case GREEN:
                color = 13;
                break;
            case RED:
                color = 14;
                break;
            case BLACK:
            default:
                color = 15;
        }

        compound.addValue( "id", "Bed" );
        compound.addValue( "color", color );
    }

    @Override
    public void update( long currentMillis, float dF ) {

    }

}
