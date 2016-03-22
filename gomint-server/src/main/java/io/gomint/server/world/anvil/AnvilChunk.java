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
import io.gomint.taglib.NBTStream;
import io.gomint.taglib.NBTStreamListener;
import io.gomint.world.Biome;
import io.gomint.world.Block;

import java.awt.*;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
	boolean               dirty;
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
	private byte[] biomeColors = new byte[16 * 16 * 3];

	// Blocks
	private byte[]      blocks     = new byte[PEWorldConstraints.BLOCKS_PER_CHUNK];
	private NibbleArray data       = new NibbleArray( PEWorldConstraints.BLOCKS_PER_CHUNK );
	private NibbleArray blockLight = new NibbleArray( PEWorldConstraints.BLOCKS_PER_CHUNK );
	private NibbleArray skyLight   = new NibbleArray( PEWorldConstraints.BLOCKS_PER_CHUNK );
	private NibbleArray height     = new NibbleArray( 16 * 16 );

	// Players / Chunk GC
	private List<EntityPlayer> players               = new ArrayList<>();
	private long               lastPlayerOnThisChunk = -1;
	private long               loadedTime            = System.currentTimeMillis();

    /**
     * Generate a new Chunk with no blocks.
     *
     * @param world The world in which this Chunk resides
     */
	public AnvilChunk( AnvilWorldAdapter world ) {
		this.world = world;
		Arrays.fill( this.biomes, (byte) 1 );
	}

    /**
     * Load a Chunk from a NBTTagCompund. This is used when loaded from a Regionfile.
     *
     * @param world     The world in which this Chunk resides
     * @param nbtStream The NBT Stream which reads and emits data from the chunk
     */
	public AnvilChunk( AnvilWorldAdapter world, NBTStream nbtStream ) {
		this( world );
		this.loadFromNBT( nbtStream );
		this.dirty = false;
	}

	// ==================================== MANIPULATION ==================================== //

	@Override
	public void packageChunk( Delegate2<Long, Packet> callback ) {
		if ( !this.dirty && this.cachedPacket != null ) {
			Packet packet = this.cachedPacket.get();
			if ( packet != null ) {
				callback.invoke( CoordinateUtils.toLong( x, z ), packet );
			} else {
                this.world.notifyPackageChunk( x, z, callback );
            }
		} else {
            this.world.notifyPackageChunk( x, z, callback );
        }
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
        int secondsAfterLeft = this.world.getServer().getServerConfig().getSecondsUntilGCAfterLastPlayerLeft();
        int waitAfterLoad = this.world.getServer().getServerConfig().getWaitAfterLoadForGCSeconds();

        return System.currentTimeMillis() - this.loadedTime > TimeUnit.SECONDS.toMillis( waitAfterLoad ) &&
                this.players.isEmpty() &&
                this.lastPlayerOnThisChunk > -1 &&
                System.currentTimeMillis() - this.lastPlayerOnThisChunk > TimeUnit.SECONDS.toMillis( secondsAfterLeft );
    }

    @Override
    public Collection<EntityPlayer> getPlayers() {
        return Collections.unmodifiableCollection( this.players );
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
		this.height.set( ( x << 4 ) | z, height );
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
		return this.height.get( ( x << 4 ) | z );
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
	 * @param x The x-coordinate of the block column
	 * @param z The z-coordinate of the block column
	 * @param r The red to set
     * @param g The green to set
     * @param b The blue to set
	 */
	public void setBiomeColorRGB( int x, int z, byte r, byte g, byte b ) {
        int basisIndex = ( x << 4 ) | z;

        this.biomeColors[basisIndex * 3] = r;
        this.biomeColors[basisIndex * 3 + 1] = g;
        this.biomeColors[basisIndex * 3 + 2] = b;

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
        int basisIndex = ( x << 4 ) | z;

        byte r = this.biomeColors[basisIndex * 3];
        byte g = this.biomeColors[basisIndex * 3 + 1];
        byte b = this.biomeColors[basisIndex * 3 + 2];

        return ( r << 16 ) | ( g << 8 ) | b;
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
				Color average = this.averageColorsRGB(
                        this.getBiomeColorRaw( i, k ),
                        this.getBiomeColorRaw( i, k - 1 ),
                        this.getBiomeColorRaw( i, k + 1 ),
                        this.getBiomeColorRaw( i - 1, k ),
                        this.getBiomeColorRaw( i - 1, k - 1 ),
                        this.getBiomeColorRaw( i - 1, k + 1 ),
                        this.getBiomeColorRaw( i + 1, k ),
                        this.getBiomeColorRaw( i + 1, k - 1 ),
                        this.getBiomeColorRaw( i + 1, k + 1 )
                );

				this.setBiomeColorRGB( i, k, (byte) average.getRed(), (byte) average.getGreen(), (byte) average.getBlue() );
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
		int xClamped = MathUtils.clamp( x, 0, 15 );
		int zClamped = MathUtils.clamp( z, 0, 15 );

		Biome biome = this.getBiome( xClamped, zClamped );
		if ( biome != null ) {
			return biome.getColorRGB( true, 0 );
		} else {
			throw new IllegalStateException( "Corrupt chunk: Block column has unknown biome <" + this.biomes[( xClamped << 4 ) | zClamped] + ">" );
		}
	}

	// ==================================== I/O ==================================== //

	/**
	 * Loads the chunk from the specified NBTTagCompound
	 *
	 * @param nbtStream The stream which loads the chunk
	 */
	private void loadFromNBT( NBTStream nbtStream ) {
        // Fill in default values
        this.biomes = new byte[256];
        Arrays.fill( this.biomes, (byte) -1 );

        // Wait until the nbt stream sends some data
        final int[] oldSectionIndex = new int[]{-1};
        final SectionCache section = new SectionCache();

        nbtStream.addListener( new NBTStreamListener() {
            @Override
            public void onNBTValue( String path, Object object ) {
                switch ( path ) {
                    case ".Level.xPos":
                        AnvilChunk.this.x = (int) object;
                        break;
                    case ".Level.zPos":
                        AnvilChunk.this.z = (int) object;
                        break;
                    case ".Level.Biomes":
                        AnvilChunk.this.biomes = (byte[]) object;
                        break;
                    default:
                        if ( path.startsWith( ".Level.Sections" ) ) {
                            // Parse the index
                            String[] split = path.split( "\\." );
                            int sectionIndex = Integer.parseInt( split[3] );

                            // Check if we completed a chunk
                            if ( oldSectionIndex[0] != -1 && oldSectionIndex[0] != sectionIndex ) {
                                // Load and convert this section
                                loadSection( section );

                                // Reset the cache
                                section.setAdd( null );
                                section.setBlockLight( null );
                                section.setBlocks( null );
                                section.setData( null );
                                section.setSkyLight( null );
                                section.setSectionY( 0 );
                            }

                            oldSectionIndex[0] = sectionIndex;

                            // Check what we have got from the chunk
                            switch ( split[4] ) {
                                case "Y":
                                    section.setSectionY( (byte) object << 4 );
                                    break;
                                case "Blocks":
                                    section.setBlocks( (byte[]) object );
                                    break;
                                case "Add":
                                    section.setAdd( new NibbleArray( (byte[]) object ) );
                                    break;
                                case "Data":
                                    section.setData( new NibbleArray( (byte[]) object ) );
                                    break;
                                case "BlockLight":
                                    section.setBlockLight( new NibbleArray( (byte[]) object ) );
                                    break;
                                case "SkyLight":
                                    section.setSkyLight( new NibbleArray( (byte[]) object ) );
                                    break;
                                default:
                                    break;
                            }
                        }
                }
            }
        } );

        // Start parsing the nbt tag
        try {
            nbtStream.parse();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // Load last section and calc biome colors
		this.loadSection( section );
		this.calculateBiomeColors();
	}

	/**
	 * Loads a chunk section from its raw NBT data.
	 *
	 * @param section The section to be loaded
	 */
	private void loadSection( SectionCache section ) {
		int         sectionY   = section.getSectionY();
		byte[]      blocks     = section.getBlocks();
		NibbleArray add        = section.getAdd();
		NibbleArray data       = section.getData();
		NibbleArray blockLight = section.getBlockLight();
		NibbleArray skyLight   = section.getSkyLight();

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

					int  blockId   = ( add != null ? add.get( blockIndex ) << 8 : 0 ) | blocks[blockIndex];
					byte blockData = data.get( blockIndex );

					blockId = AnvilBlockConverter.convertBlockID( blockId, blockData );
					blockData = AnvilBlockConverter.convertBlockData( blockId, blockData );

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
	public Color averageColorsRGB( int... colors ) {
		int r = 0, g = 0, b = 0;

        for ( int color : colors ) {
            r += color >> 16 & 0xff;
            g += color >> 8 & 0xff;
            b += color & 0xff;
        }

		r /= colors.length;
		g /= colors.length;
		b /= colors.length;

		return new Color( r, g, b );
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
		                                        this.height.length() +
		                                        ( this.biomeColors.length << 2 ) +
		                                        4 );

		// Preparations:
		this.calculateBiomeColors();

		buffer.writeBytes( this.blocks );
		buffer.writeBytes( this.data.raw() );
		buffer.writeBytes( this.blockLight.raw() );
		buffer.writeBytes( this.skyLight.raw() );
		buffer.writeBytes( this.height.toByteArray() );

		for ( int i = 0; i < this.biomes.length; ++i ) {
            byte r = this.biomeColors[i * 3];
            byte g = this.biomeColors[i * 3 + 1];
            byte b = this.biomeColors[i * 3 + 2];

            int color = ( r << 16 ) | ( g << 8 ) | b;

			buffer.writeInt( ( this.biomes[i] << 24 ) | ( color & 0x00FFFFFF ) );
		}

		buffer.writeInt( 0 );

		PacketWorldChunk packet = new PacketWorldChunk();
		packet.setX( this.x );
		packet.setZ( this.z );
		packet.setData( buffer.getBuffer() );
		return packet;
	}

}
