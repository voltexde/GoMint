/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.world;

import io.gomint.math.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public enum Biome {

	// ==================================== BIOMES ==================================== //
	OCEAN( 0, "Ocean", 0.5F, 0.5F ),
	PLAINS( 1, "Plains", 0.8F, 0.4F ),
	DESERT( 2, "Desert", 2.0F, 0.0F ),
	EXTREME_HILLS( 3, "Extreme Hills", 0.2F, 0.3F ),
	FOREST( 4, "Forest", 0.7F, 0.8F ),
	TAIGA( 5, "Taiga", 0.05F, 0.8F ),
	SWAMPLAND( 6, "Swampland", 0.8F, 0.9F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			// TODO: Implement Perlin noise!
			return 0x4C763C;
		}
	},
	RIVER( 7, "River", 0.5F, 0.5F ),
	NETHER( 8, "Nether", 2.0F, 0.0F ),
	END( 9, "End", 0.5F, 0.5F ),
	FROZEN_OCEAN( 10, "Frozen Ocean", 0.0F, 0.5F ),
	FROZEN_RIVER( 11, "Frozen River", 0.0F, 0.5F ),
	ICE_PLAINS( 12, "Ice Plains", 0.0F, 0.5F ),
	ICE_MOUNTAINS( 13, "Ice Mountains", 0.0F, 0.5F ),
	MUSHROOM_ISLAND( 14, "Mushroom Island", 0.9F, 1.0F ),
	MUSHROOM_ISLAND_SHORE( 15, "Mushroom Island Shore", 0.9F, 1.0F ),
	BEACH( 16, "Beach", 0.8F, 0.4F ),
	DESERT_HILLS( 17, "Desert Hills", 2.0F, 0.0F ),
	FOREST_HILLS( 18, "Forest Hills", 0.7F, 0.8F ),
	TAIGA_HILLS( 19, "Taiga Hills", 0.2F, 0.7F ),
	EXTREME_HILLS_EDGE( 20, "Extreme Hills Edge", 0.2F, 0.3F ),
	JUNGLE( 21, "Jungle", 1.2F, 0.9F ),
	JUNGLE_HILLS( 22, "Jungle Hills", 1.2F, 0.9F ),
	JUNGLE_EDGE( 23, "Jungle Edge", 0.95F, 0.8F ),
	DEEP_OCEAN( 24, "Deep Ocean", 0.5F, 0.5F ),
	STONE_BEACH( 25, "Stone Beach", 0.2F, 0.3F ),
	COLD_BEACH( 26, "Cold Beach", 0.05F, 0.3F ),
	BIRCH_FOREST( 27, "Birch Forest", 0.6F, 0.6F ),
	BIRCH_FOREST_HILLS( 28, "Birch Forest Hills", 0.6F, 0.6F ),
	ROOFED_FOREST( 29, "Roofed Forest", 0.7F, 0.8F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			int color = super.getColorRGB( grass, height );
			return ( grass ? ( ( ( color & 0xFEFEFE ) + ROOFED_FOREST_MODIFIER ) / 2 ) : color );
		}
	},
	COlD_TAIGA( 30, "Cold Taiga", -0.5F, 0.4F ),
	COLD_TAIGA_HILLS( 31, "Cold Taiga Hills", -0.5F, 0.4F ),
	MEGA_TAIGA( 32, "Mega Taiga", 0.3F, 0.8F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			return ( grass ? MESA_GRASS_COLOR : MESA_FOLIAGE_COLOR );
		}
	},
	MEGA_TAIGA_HILLS( 33, "Mega Taiga Hills", 0.3F, 0.8F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			return ( grass ? MESA_GRASS_COLOR : MESA_FOLIAGE_COLOR );
		}
	},
	EXTREME_HILLS_PLUS( 34, "Extreme Hills+", 0.2F, 0.3F ),
	SAVANNA( 35, "Savanna", 1.2F, 0.0F ),
	SAVANNA_PLATEAU( 36, "Savanna Plateau", 1.0F, 0.0F ),
	MESA( 37, "Mesa", 2.0F, 0.0F ),
	MESA_PLATEAU_F( 38, "Mesa Plateau F", 2.0F, 0.0F ),
	MESA_PLATEAU( 39, "Mesa Plateau", 2.0F, 0.0F ),
	THE_VOID( 127, "The Void", 0.8F, 0.4F ),
	UNKNOWN_1( 128, "Unknown 1", 0.8F, 0.4F ),
	SUNFLOWER_PLAINS( 129, "Sunflower Plains", 0.8F, 0.4F ),
	DESERT_M( 130, "Desert M", 2.0F, 0.0F ),
	EXTREME_HILLS_M( 131, "Extreme Hills M", 0.2F, 0.3F ),
	FLOWER_FOREST( 132, "Flower Forest", 0.7F, 0.8F ),
	TAIGA_M( 133, "Taiga M", 0.5F, 0.8F ),
	SWAMPLAND_M( 134, "Swampland M", 0.8F, 0.9F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			// TODO: Implement Perlin noise!
			return 0x4C763C;
		}
	},
	ICE_PLAINS_SPIKES( 140, "Ice Plains Spikes", 0.0F, 0.5F ),
	JUNGLE_M( 149, "Jungle M", 1.2F, 0.9F ),
	UNKNOWN_2( 150, "Unknown 2", 0.8F, 0.4F ),
	JUNGLE_EDGE_M( 151, "Jungle Edge M", 0.95F, 0.8F ),
	BIRCH_FOREST_M( 155, "Birch Forest M", 0.6F, 0.6F ),
	BIRCH_FOREST_HILLS_M( 156, "Birch Forest Hills M", 0.6F, 0.6F ),
	ROOFED_FOREST_M( 157, "Roofed Forest M", 0.7F, 0.8F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			int color = super.getColorRGB( grass, height );
			return ( grass ? ( ( ( color & 0xFEFEFE ) + ROOFED_FOREST_MODIFIER ) / 2 ) : color );
		}
	},
	COLD_TAIGA_M( 158, "Cold Taiga M", -0.5F, 0.4F ),
	MEGA_SPRUCE_TAIGA( 160, "Mega Spruce Taiga", 0.25F, 0.8F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			return ( grass ? MESA_GRASS_COLOR : MESA_FOLIAGE_COLOR );
		}
	},
	MEGA_SPRUCE_TAIGA_HILLS( 161, "Mega Spruce Taiga Hills", 0.3F, 0.8F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			return ( grass ? MESA_GRASS_COLOR : MESA_FOLIAGE_COLOR );
		}
	},
	EXTREME_HILLS_PLUS_M( 162, "Extreme Hills+ M", 0.2F, 0.3F ),
	SAVANNA_M( 163, "Savanna M", 1.2F, 0.0F ),
	SAVANNA_PLATEAU_M( 164, "Savanna Plateau M", 1.0F, 0.0F ),
	MESA_BRYCE( 165, "Mesa (Bryce)", 2.0F, 0.0F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			return ( grass ? MESA_GRASS_COLOR : MESA_FOLIAGE_COLOR );
		}
	},
	MESA_PLATEAU_F_M( 166, "Mesa Plateau F M", 2.0F, 0.0F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			return ( grass ? MESA_GRASS_COLOR : MESA_FOLIAGE_COLOR );
		}
	},
	MESA_PLATEAU_M( 167, "Mesa Plateau M", 2.0F, 0.0F ) {
		@Override
		public int getColorRGB( boolean grass, int height ) {
			return ( grass ? MESA_GRASS_COLOR : MESA_FOLIAGE_COLOR );
		}
	};

	// ==================================== CONSTANTS ==================================== //
	public static final int ROOFED_FOREST_MODIFIER = 0x28340A;
	public static final int MESA_GRASS_COLOR       = 0x90814D;
	public static final int MESA_FOLIAGE_COLOR     = 0x9E814D;

	private static final double[] GRASS_INTERPOLATION_COLORS = new double[] { 0.5D, 0.703125D, 0.58984375D,               // Bottom right corner
	                                                                          0.74609375D, 0.71484375D, 0.33203125D,      // Bottom left corner
	                                                                          0.27734375D, 0.80078125D, 0.19921875D       // Upper left corner
	};

	private static final double[] FOLIAGE_INTERPOLATION_COLORS = new double[] { 0.375D, 0.62890625D, 0.48046875D,           // Bottom right corner
	                                                                            0.6796875D, 0.640625D, 0.1640625D,          // Bottom left corner
	                                                                            0.1015625D, 0.74609375D, 0.0D               // Upper left corner
	};

	// ==================================== FIELDS ==================================== //
	private static final Map<Integer, Biome> biomesById = new HashMap<>();

	/**
	 * Attempts to get a biome given its id.
	 *
	 * @param id The ID of the biome
	 * @return The biome if found or null otherwise
	 */
	public static Biome getBiomeById( int id ) {
		return biomesById.get( id );
	}

	private final int    id;
	private final String name;
	private final float  temperature;
	private final float  downfall;


	Biome( int id, String name, float temperature, float downfall ) {
		this.id = id;
		this.name = name;
		this.temperature = temperature;
		this.downfall = downfall;
	}

	/**
	 * Gets the unique ID of the biome.
	 *
	 * @return The biome's unique ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the name of the biome.
	 *
	 * @return The biome's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the biome's temperature.
	 *
	 * @return The biome's temperature
	 */
	public float getTemperature() {
		return this.temperature;
	}

	/**
	 * Gets the biome's downfall rate.
	 *
	 * @return The biome's downfall rate
	 */
	public float getDownfall() {
		return this.downfall;
	}

	/**
	 * Gets a biome's RGB color given the height of a block.
	 * <p>
	 * A biome's color is calculated using a linearly interpolating image called grass.png or foliage.png
	 * depending on whether the color is to be applied on grass or other things such as leaves. The coordinates
	 * to be used on that image are calculated by taking into account the temperature and the downfall
	 * rate. The temperature decreases naturally by 0.00166667 every meter above the water level (=64).
	 * The downfall value then gets multiplied by the temperature. Afterwards the temperature will resemble
	 * the U-coordinate, the downfall value will resemble the V-coordinate.
	 * </p>
	 *
	 * @param grass  Whether or not the color is to be applied on grass or not (foliage if set to false)
	 * @param height The height of the block to get the biome's color for
	 *
	 * @return An integer encoding the biome color
	 */
	public int getColorRGB( boolean grass, int height ) {
		// Calculate temperature and downfall rate:
		float temperature = MathUtils.clamp( this.getTemperature() - ( height > 64 ? ( height - 64 ) * 0.00166667F : 0.0F ), 0.0F, 1.0F );
		float downfall    = MathUtils.clamp( this.getDownfall(), 0.0F, 1.0F );
		downfall *= temperature;

		// Interpolate on triangle:
		double   r      = 0.0D, g = 0.0D, b = 0.0D;
		double[] lambda = new double[3];
		lambda[0] = downfall;
		lambda[1] = temperature - downfall;
		lambda[2] = 1.0D - temperature;

		double[] colors = ( grass ? GRASS_INTERPOLATION_COLORS : FOLIAGE_INTERPOLATION_COLORS );
		for ( int i = 0; i < 3; ++i ) {
			r += lambda[i] * colors[i * 3 + 0];
			g += lambda[i] * colors[i * 3 + 1];
			b += lambda[i] * colors[i * 3 + 2];
		}

		// Clamp resulting color:
		int ri = MathUtils.clamp( (int) ( r * 255.0D ), 0, 255 );
		int gi = MathUtils.clamp( (int) ( g * 255.0D ), 0, 255 );
		int bi = MathUtils.clamp( (int) ( b * 255.0D ), 0, 255 );
		return ( ri << 16 ) | ( gi << 8 ) | bi;
	}

	static {
		for ( Biome biome : Biome.values() ) {
			biomesById.put( biome.getId(), biome );
		}
	}

}