package io.gomint.server.world;

import io.gomint.math.Location;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.world.block.Blocks;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.openhft.koloboke.collect.ObjCollection;
import net.openhft.koloboke.collect.map.IntObjMap;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

import java.io.ByteArrayOutputStream;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
class ChunkSlice {

    private static final ThreadLocal<Location> LOCATION_CACHE = new ThreadLocal<>();

    @Getter private final ChunkAdapter chunk;
    @Getter private final int sectionY;

    private boolean isAllAir = true;

    private byte[] blocks = null;
    private NibbleArray data = null;
    private NibbleArray blockLight = new NibbleArray( 4096 );
    private NibbleArray skyLight = new NibbleArray( 4096 );

    private IntObjMap<TileEntity> tileEntities = HashIntObjMaps.newMutableMap();

    private int getIndex( int x, int y, int z ) {
        return ( x * 256 ) + ( z * 16 ) + y;
    }

    byte getBlock( int x, int y, int z ) {
        if ( this.isAllAir ) {
            return 0;
        }

        return this.blocks[getIndex( x, y, z )];
    }

    <T extends io.gomint.world.block.Block> T getBlockInstance( int x, int y, int z ) {
        int index = getIndex( x, y, z );

        int fullX = ( this.chunk.getX() * 16 ) + x;
        int fullY = ( sectionY * 16 ) + y;
        int fullZ = ( this.chunk.getZ() * 16 ) + z;

        Location loc;
        if ( ( loc = LOCATION_CACHE.get() ) == null ) {
            LOCATION_CACHE.set( loc = new Location( this.chunk.world, fullX, fullY, fullZ ) );
        }

        loc.setWorld( this.chunk.world );
        loc.setX( fullX );
        loc.setY( fullY );
        loc.setZ( fullZ );

        if ( isAllAir ) {
            return (T) Blocks.get( 0, (byte) 0, this.skyLight.get( index ), this.blockLight.get( index ), null, loc );
        }

        return (T) Blocks.get( this.blocks[index] & 0xFF, this.data == null ? 0 : this.data.get( index ), this.skyLight.get( index ),
                this.blockLight.get( index ), this.tileEntities.get( index ), loc );
    }

    ObjCollection<TileEntity> getTileEntities() {
        return tileEntities.values();
    }

    void addTileEntity( int x, int y, int z, TileEntity tileEntity ) {
        int index = getIndex( x, y, z );
        this.tileEntities.put( index, tileEntity );
    }

    void setBlock( int x, int y, int z, byte blockId ) {
        int index = getIndex( x, y, z );

        if ( blockId != 0 ) {
            if ( this.blocks == null ) {
                this.blocks = new byte[4096];
                this.isAllAir = false;
            }
        }

        if ( this.blocks != null ) {
            this.blocks[index] = blockId;
        }
    }

    void setData( int x, int y, int z, byte data ) {
        int index = getIndex( x, y, z );

        if ( !this.isAllAir ) {
            if ( this.data == null ) {
                this.data = new NibbleArray( 4096 );
            }
        }

        this.data.set( index, data );
    }

    byte getData( int x, int y, int z ) {
        return this.data.get( getIndex( x, y, z ) );
    }

    void setBlockLight( int x, int y, int z, byte value ) {
        this.blockLight.set( getIndex( x, y, z ), value );
    }

    byte getBlockLight( int x, int y, int z ) {
        return this.blockLight.get( getIndex( x, y, z ) );
    }

    void setSkyLight( int x, int y, int z, byte value ) {
        this.skyLight.set( getIndex( x, y, z ), value );
    }

    byte getSkyLight( int x, int y, int z ) {
        return this.skyLight.get( getIndex( x, y, z ) );
    }

    boolean isAllAir() {
        return this.isAllAir;
    }

    byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write( this.blocks == null ? new byte[4096] : this.blocks );
            baos.write( this.data == null ? new byte[2048] : this.data.raw() );
            baos.write( this.skyLight.raw() );
            baos.write( this.blockLight.raw() );
        } catch ( Exception ignored ) {

        }

        return baos.toByteArray();
    }

}
