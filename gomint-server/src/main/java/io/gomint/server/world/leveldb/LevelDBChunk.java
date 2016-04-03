/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.leveldb;

import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.NibbleArray;
import io.gomint.server.world.PEWorldConstraints;
import io.gomint.server.world.WorldAdapter;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author geNAZt
 * @version 1.0
 */
public class LevelDBChunk extends ChunkAdapter {

    public LevelDBChunk( WorldAdapter worldAdapter, byte[] chunkData, int x, int z ) {
        this.world = worldAdapter;
        this.x = x;
        this.z = z;

        // Load chunk
        this.loadChunk( chunkData );
        this.calculateBiomeColors();
        this.calculateHeightmap();
    }

    private void loadChunk( byte[] chunkData ) {
        // Wrap data and read blocks
        ByteBuffer buffer = ByteBuffer.wrap( chunkData ).order( ByteOrder.BIG_ENDIAN );
        buffer.get( this.blocks );

        // Read in data
        byte[] data = new byte[16384];
        buffer.get( data );
        this.data = new NibbleArray( data );

        // Read in skylight
        byte[] skyLight = new byte[16384];
        buffer.get( skyLight );
        this.skyLight = new NibbleArray( skyLight );

        // Read in blocklight
        byte[] blockLight = new byte[16384];
        buffer.get( blockLight );
        this.blockLight = new NibbleArray( blockLight );

        // Read in height
        for ( int i = 0; i < 256; i++ ) {
            this.height.set( i, buffer.get() );
        }

        // Read in color
        int c = 0;
        for ( int i = 0; i < 256; i++ ) {
            int biome = buffer.getInt();
            Color color = new Color( biome );
            this.biomes[i] = (byte) (biome >> 24);
            this.biomeColors[c++] = (byte) color.getRed();
            this.biomeColors[c++] = (byte) color.getGreen();
            this.biomeColors[c++] = (byte) color.getBlue();
        }
    }

}
