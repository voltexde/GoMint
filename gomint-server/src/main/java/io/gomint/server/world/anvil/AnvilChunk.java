/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.MathUtils;
import io.gomint.server.async.Delegate2;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketWorldChunk;
import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.CoordinateUtils;
import io.gomint.server.world.PEWorldConstraints;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.Biome;
import io.gomint.world.Block;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class AnvilChunk extends ChunkAdapter {

    // Networking
    /**
     * Dirty state of this cached packet. True when it needs to be repacked
     */
    boolean dirty;
    /**
     * Cached packet version of this chunk
     */
    SoftReference<Packet> cachedPacket;

    // World
	private final AnvilWorldAdapter world;

	// Chunk
	private int x;
	private int z;

	// Biomes
	private byte[] biomes      = new byte[16 * 16];
	private int[]  biomeColors = new int[16 * 16];

	// Blocks
	private byte[]      blocks     = new byte[PEWorldConstraints.BLOCKS_PER_CHUNK];
	private NibbleArray data       = new NibbleArray( PEWorldConstraints.BLOCKS_PER_CHUNK );
	private NibbleArray blockLight = new NibbleArray( PEWorldConstraints.BLOCKS_PER_CHUNK );
	private NibbleArray skyLight   = new NibbleArray( PEWorldConstraints.BLOCKS_PER_CHUNK );
	private byte[]      height     = new byte[16 * 16];

    // Players / Chunk GC
    private List<EntityPlayer> players = new ArrayList<>();
    private long lastPlayerOnThisChunk = -1;
    private long loadedTime = System.currentTimeMillis();

	public AnvilChunk( AnvilWorldAdapter world ) {
		this.world = world;
		Arrays.fill( this.biomes, (byte) 1 );
	}

	public AnvilChunk( AnvilWorldAdapter world, NBTTagCompound data ) {
		this( world );
		this.loadFromNBT( data );
		this.dirty = false;
	}

	// ==================================== MANIPULATION ==================================== //

	@Override
	public void packageChunk( Delegate2<Long, Packet> callback ) {
		if ( !this.dirty && this.cachedPacket != null ) {
            Packet packet = this.cachedPacket.get();
            if ( packet != null ) {
                callback.invoke( CoordinateUtils.toLong( x, z ), packet );
            }

			return;
		}

		this.world.notifyPackageChunk( this, callback );
	}

    @Override
    public void addPlayer( EntityPlayer player ) {
        this.players.add( player );
    }

    @Override
    public void removePlayer( EntityPlayer player ) {
        this.players.remove( player );
        this.lastPlayerOnThisChunk = System.currentTimeMillis();
    }

    @Override
    public boolean canBeGCed() {
        return System.currentTimeMillis() - this.loadedTime > TimeUnit.SECONDS.toMillis( this.world.getServer().getServerConfig().getWaitAfterLoadForGCSeconds() ) &&
                this.players.isEmpty() &&
                System.currentTimeMillis() - this.lastPlayerOnThisChunk > TimeUnit.SECONDS.toMillis( this.world.getServer().getServerConfig().getSecondsUntilGCAfterLastPlayerLeft() );
    }

    /**
	 * Gets the x-coordinate of the chunk.
	 *
	 * @return The chunk's x-coordinate
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Gets the z-coordinate of the chunk.
	 *
	 * @return The chunk's z-coordinate
	 */
	public int getZ() {
		return this.z;
	}

	/**
	 * Sets the ID of a block at the specified coordinates given in chunk coordinates.
	 *
	 * @param x  The x-coordinate of the block
	 * @param y  The y-coordinate of the block
	 * @param z  The z-coordinate of the block
	 * @param id The ID to set the block to
	 */
	public void setBlock( int x, int y, int z, int id ) {
		this.blocks[( x * 2048 ) + ( z * 128 ) + y] = (byte) id;
		this.dirty = true;
	}

	/**
	 * Sets the ID of a block at the specified coordinates given in chunk coordinates.
	 *
	 * @param x The x-coordinate of the block
	 * @param y The y-coordinate of the block
	 * @param z The z-coordinate of the block
	 *
	 * @return The ID of the block
	 */
	public byte getBlock( int x, int y, int z ) {
		return this.blocks[( x << 11 ) | ( z << 7 ) | y];
	}

	/**
	 * Sets the metadata value of the block at the specified coordinates.
	 *
	 * @param x    The x-coordinate of the block
	 * @param y    The y-coordinate of the block
	 * @param z    The z-coordinate of the block
	 * @param data The data value to set
	 */
	public void setData( int x, int y, int z, byte data ) {
		this.data.set( ( x * 2048 ) + ( z * 128 ) + y, data );
		this.dirty = true;
	}

	/**
	 * Gets the metadata value of the block at the specified coordinates.
	 *
	 * @param x The x-coordinate of the block
	 * @param y The y-coordinate of the block
	 * @param z The z-coordinate of the block
	 *
	 * @return The data value of the block
	 */
	public byte getData( int x, int y, int z ) {
		return this.data.get( ( x << 11 ) | ( z << 7 ) | y );
	}

	/**
	 * Sets the maximum block height at a specific coordinate-pair.
	 *
	 * @param x      The x-coordinate relative to the chunk
	 * @param z      The z-coordinate relative to the chunk
	 * @param height The maximum block height
	 */
	private void setHeight( int x, int z, byte height ) {
		this.height[( x << 4 ) | z] = height;
		this.dirty = true;
	}

	/**
	 * Gets the maximum block height at a specific coordinate-pair. Requires the height
	 * map to be up-to-date.
	 *
	 * @param x The x-coordinate relative to the chunk
	 * @param z The z-coordinate relative to the chunk
	 *
	 * @return The maximum block height
	 */
	public byte getHeight( int x, int z ) {
		return this.height[( x << 4 ) | z];
	}

	/**
	 * Sets the lighting value of the specified block
	 *
	 * @param x     The x-coordinate of the block
	 * @param y     The y-coordinate of the block
	 * @param z     The z-coordinate of the block
	 * @param value The lighting value
	 */
	private void setBlockLight( int x, int y, int z, byte value ) {
		this.blockLight.set( ( x * 2048 ) + ( z * 128 ) + y, value );
		this.dirty = true;
	}

	/**
	 * Gets the lighting value of the specified block
	 *
	 * @param x The x-coordinate of the block
	 * @param y The y-coordinate of the block
	 * @param z The z-coordinate of the block
	 *
	 * @return The block's lighting value
	 */
	public byte getBlockLight( int x, int y, int z ) {
		return this.blockLight.get( ( x << 11 ) | ( z << 7 ) | y );
	}

	/**
	 * Sets the skylight value of the specified block
	 *
	 * @param x     The x-coordinate of the block
	 * @param y     The y-coordinate of the block
	 * @param z     The z-coordinate of the block
	 * @param value The lighting value
	 */
	private void setSkyLight( int x, int y, int z, byte value ) {
		this.skyLight.set( ( x * 2048 ) + ( z * 128 ) + y, value );
		this.dirty = true;
	}

	/**
	 * Gets the skylight value of the specified block
	 *
	 * @param x The x-coordinate of the block
	 * @param y The y-coordinate of the block
	 * @param z The z-coordinate of the block
	 *
	 * @return The block's lighting value
	 */
	public byte getSkyLight( int x, int y, int z ) {
		return this.skyLight.get( ( x << 11 ) | ( z << 7 ) | y );
	}

	/**
	 * Sets a block column's biome.
	 *
	 * @param x     The x-coordinate of the block column
	 * @param z     The z-coordinate of the block column
	 * @param biome The biome to set
	 */
	private void setBiome( int x, int z, Biome biome ) {
		this.biomes[( x << 4 ) | z] = (byte) biome.getId();
		this.dirty = true;
	}

	/**
	 * Gets a block column's biome.
	 *
	 * @param x The x-coordinate of the block column
	 * @param z The z-coordinate of the block column
	 *
	 * @return The block column's biome
	 */
	public Biome getBiome( int x, int z ) {
		return Biome.getBiomeById( this.biomes[( x << 4 ) | z] );
	}

	/**
	 * Gets the RGB color of the block column at the specified coordinates.
	 *
	 * @param x   The x-coordinate of the block column
	 * @param z   The z-coordinate of the block column
	 * @param rgb The color to set
	 */
	public void setBiomeColorRGB( int x, int z, int rgb ) {
		this.biomeColors[( x << 4 ) | z] = rgb & 0xFFFFFF;
		this.dirty = true;
	}

	/**
	 * Gets the RGB color of the block column at the specified coordinates.
	 *
	 * @param x The x-coordinate of the block column
	 * @param z The z-coordinate of the block column
	 *
	 * @return The block column's color
	 */
	public int getBiomeColorRGB( int x, int z ) {
		return this.biomeColors[( x << 4 ) | z];
	}

	@Override
	public Block getBlockAt( int x, int y, int z ) {
		return null;
	}

	/**
	 * Recalculates the height map of the chunk.
	 */
	public void calculateHeightmap() {
		for ( int i = 0; i < 16; ++i ) {
			for ( int k = 0; k < 16; ++k ) {
				for ( int j = PEWorldConstraints.MAX_BUILD_HEIGHT - 1; j > 0; --j ) {
					if ( this.getBlock( i, j, k ) != 0 ) {
						this.setHeight( i, k, (byte) j );
						break;
					}
				}
			}
		}
	}

	/**
	 * Recalculates the biome colors of the chunk by interpolating between
	 * each block's adjacent biomes. This operation is expensive so use with
	 * care!
	 */
	public void calculateBiomeColors() {
		for ( int i = 0; i < 16; ++i ) {
			for ( int k = 0; k < 16; ++k ) {
				int average = this.averageColorsRGB( this.getBiomeColorRaw( i, k ), this.getBiomeColorRaw( i, k - 1 ), this.getBiomeColorRaw( i, k + 1 ), this.getBiomeColorRaw( i - 1, k ), this.getBiomeColorRaw( i - 1, k - 1 ), this.getBiomeColorRaw( i - 1, k + 1 ), this.getBiomeColorRaw( i + 1, k ), this.getBiomeColorRaw( i + 1, k - 1 ), this.getBiomeColorRaw( i + 1, k + 1 ) );
				this.setBiomeColorRGB( i, k, average );
			}
		}
	}

	/**
	 * Gets the raw biome color from a block's biome.
	 *
	 * @return The block's raw biome color
	 */
	private int getBiomeColorRaw( int x, int z ) {
		// Fix bad parameters:
		x = MathUtils.clamp( x, 0, 15 );
		z = MathUtils.clamp( z, 0, 15 );

		Biome biome = this.getBiome( x, z );
		if ( biome != null ) {
			return biome.getColorRGB( true, 0 );
		} else {
			throw new IllegalStateException( "Corrupt chunk: Block column has unknown biome <" + this.biomes[( x << 4 ) | z] + ">" );
		}
	}

	// ==================================== I/O ==================================== //

	/**
	 * Loads the chunk from the specified NBTTagCompound
	 *
	 * @param data The compound to load the chunk from
	 */
	private void loadFromNBT( NBTTagCompound data ) {
		NBTTagCompound level = data.getCompound( "Level", false );
		if ( level == null ) {
			throw new IllegalArgumentException( "Corrupt chunk: Missing 'Level' compound" );
		}

		this.x = level.getInteger( "xPos", 0 );
		this.z = level.getInteger( "zPos", 0 );
		this.biomes = level.getByteArray( "Biomes", null );
		if ( this.biomes == null ) {
			this.biomes = new byte[256];
			Arrays.fill( this.biomes, (byte) -1 );
		}

		this.loadSections( level.getList( "Sections", false ) );

		this.calculateHeightmap();
		this.calculateBiomeColors();
	}

	/**
	 * Loads the sections of a chunk given the sections' raw NBT data
	 *
	 * @param sections The sections' raw NBT data
	 */
	private void loadSections( List<Object> sections ) {
		for ( Object section : sections ) {
			if ( section instanceof NBTTagCompound ) {
				this.loadSection( (NBTTagCompound) section );
			}
		}
	}

	/**
	 * Loads a chunk section from its raw NBT data.
	 *
	 * @param section The section to be loaded
	 */
	private void loadSection( NBTTagCompound section ) {
		int         sectionY   = section.getByte( "Y", (byte) 0 ) << 4;
		byte[]      blocks     = section.getByteArray( "Blocks", null );
		NibbleArray add        = ( section.containsKey( "Add" ) ? new NibbleArray( section.getByteArray( "Add", null ) ) : new NibbleArray( 4096 ) );
		NibbleArray data       = ( section.containsKey( "Data" ) ? new NibbleArray( section.getByteArray( "Data", null ) ) : null );
		NibbleArray blockLight = ( section.containsKey( "BlockLight" ) ? new NibbleArray( section.getByteArray( "BlockLight", null ) ) : null );
		NibbleArray skyLight   = ( section.containsKey( "SkyLight" ) ? new NibbleArray( section.getByteArray( "SkyLight", null ) ) : null );

		if ( blocks == null || data == null || blockLight == null || skyLight == null ) {
			throw new IllegalArgumentException( "Corrupt chunk: Section is missing obligatory compounds" );
		}

		for ( int j = 0; j < 16; ++j ) {
			for ( int i = 0; i < 16; ++i ) {
				for ( int k = 0; k < 16; ++k ) {
					int y = sectionY + j;
					if ( y < 0 || y >= PEWorldConstraints.MAX_BUILD_HEIGHT ) {
						continue;
					}

					int blockIndex = j << 8 | k << 4 | i;

					int blockId    = ( add.get( blockIndex ) << 8 ) | blocks[blockIndex];
                    byte blockData = data.get( blockIndex );

                    if ( AnvilBlockConverter.needsToConvert( blockId, blockData ) ) {
                        blockId = AnvilBlockConverter.convertBlockID( blockId, blockData );
                        blockData = AnvilBlockConverter.convertBlockData( blockId, blockData );
                    }

					this.setBlock( i, y, k, blockId );
					this.setData( i, y, k, blockData );
					this.setBlockLight( i, y, k, blockLight.get( blockIndex ) );
					this.setSkyLight( i, y, k, skyLight.get( blockIndex ) );
				}
			}
		}
	}

	// ==================================== MISCELLANEOUS ==================================== //

	/**
	 * Averages multiple RGB colors linearly into one combined color.
	 *
	 * @param colors All colors to combine
	 *
	 * @return The combined color
	 */
	public int averageColorsRGB( int... colors ) {
		int r = 0, g = 0, b = 0;
		for ( int i = 0; i < colors.length; ++i ) {
			r += colors[i] & 0xFF0000;
			g += colors[i] & 0x00FF00;
			b += colors[i] & 0x0000FF;
		}
		r /= colors.length;
		g /= colors.length;
		b /= colors.length;
		return ( r << 16 ) | ( g << 8 ) | b;
	}

	/**
	 * Invoked by the world's asynchronous worker thread once the chunk is supposed
	 * to actually pack itself into a world chunk packet.
	 *
	 * @return The world chunk packet that is to be sent
	 */
	PacketWorldChunk createPackagedData() {
		PacketBuffer buffer = new PacketBuffer( this.blocks.length +
		                                        this.data.raw().length +
		                                        this.blockLight.raw().length +
		                                        this.skyLight.raw().length +
		                                        this.height.length +
		                                        ( this.biomeColors.length << 2 ) +
												4 );

		// Preparations:
		this.calculateBiomeColors();

		buffer.writeBytes( this.blocks );
		buffer.writeBytes( this.data.raw() );
		buffer.writeBytes( this.blockLight.raw() );
		buffer.writeBytes( this.skyLight.raw() );
		buffer.writeBytes( this.height );
		for ( int i = 0; i < this.biomeColors.length; ++i ) {
			buffer.writeInt( ( this.biomes[i] << 24 ) | ( this.biomeColors[i] & 0x00FFFFFF ) );
		}
		buffer.writeInt( 0 );

		PacketWorldChunk packet = new PacketWorldChunk();
		packet.setX( this.x );
		packet.setZ( this.z );
		packet.setData( buffer.getBuffer() );
		return packet;
	}
}
