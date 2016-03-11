/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world.anvil;

import io.gomint.taglib.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class RegionFile {

	private final RandomAccessFile file;

	/**
	 * Constructs a new region file that will load from the specified file.
	 *
	 * @param file The file to load
	 *
	 * @throws IOException Thrown in case the file was not found / could not be opened
	 */
	public RegionFile( File file ) throws IOException {
		this.file = new RandomAccessFile( file, "rw" );
	}

	/**
	 * Attempts to load an anvil chunk from the NBT data found inside this region file
	 *
	 * @param x The x-coordinate of the chunk
	 * @param z The z-coordinate of the chunk
	 *
	 * @return The chunk if found
	 * @throws IOException Thrown in case an I/O error occurs or the chunk was not found
	 */
	public AnvilChunk loadChunk( int x, int z ) throws IOException {
		int fileOffset = ( ( x & 31 ) + ( ( z & 31 ) << 5 ) ) << 2;

		// Navigate to entry in location table:
		this.file.seek( fileOffset );

		byte[] buffer = new byte[4];
		this.file.read( buffer, 0, 3 );

		int offset = ( ( (int) buffer[0] << 16 & 0xFF0000 ) | ( (int) buffer[1] << 8 & 0xFF00 ) | ( (int) buffer[2] & 0xFF ) );

		int length = this.file.read();
		if ( offset == 0 || length == 0 ) {
			throw new IOException( "Chunk not found inside region" );
		}

		fileOffset = offset << 12;

		// Seek chunk data:
		this.file.seek( fileOffset );
		this.file.read( buffer, 0, 4 );

		int    exactLength = ( ( (int) buffer[0] << 24 & 0xFF000000 ) | ( (int) buffer[1] << 16 & 0xFF0000 ) | ( (int) buffer[2] << 8 & 0xFF00 ) | ( (int) buffer[3] & 0xFF ) );
		int    compressionScheme = this.file.read();
		byte[] nbtBuffer   = new byte[exactLength];
		this.file.read( nbtBuffer );

		InputStream input = null;
		switch ( compressionScheme ) {
			case 1:
				input = new GZIPInputStream( new ByteArrayInputStream( nbtBuffer ) );
				break;

			case 2:
				input = new InflaterInputStream( new ByteArrayInputStream( nbtBuffer ) );
				break;
		}

		NBTTagCompound nbt = NBTTagCompound.readFrom( input, false, ByteOrder.BIG_ENDIAN );
		return new AnvilChunk( nbt );
	}
	
}
