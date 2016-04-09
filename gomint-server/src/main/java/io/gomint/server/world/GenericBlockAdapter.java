package io.gomint.server.world;

import io.gomint.world.Block;

/**
 * Generic implementation of the Block interface of the GoMint API. Compatible
 * with all supported world types.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class GenericBlockAdapter implements Block {

	// Referencing world instead of chunk here, as chunk might have been
	// unloaded while the block is still in use:
	private final WorldAdapter world;
	private final int x;
	private final int y;
	private final int z;

	/**
	 * Constructs a new generic block adapter that may be used for easier block manipulation.
	 *
	 * @param world The world the block resides in
	 * @param x The x coordinate of the block
	 * @param y The y coordinate of the block
	 * @param z The z coordinate of the block
	 */
	GenericBlockAdapter( WorldAdapter world, int x, int y, int z ) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int getBlockId() {
		final ChunkAdapter chunk = this.world.getChunk( CoordinateUtils.fromBlockToChunk( this.x ), CoordinateUtils.fromBlockToChunk( this.z ) );
		if ( chunk == null ) {
			return 0;
		}
		return chunk.getBlock( this.x & 0xF, this.y, this.z & 0xF );
	}

	@Override
	public byte getBlockData() {
		final ChunkAdapter chunk = this.world.getChunk( CoordinateUtils.fromBlockToChunk( this.x ), CoordinateUtils.fromBlockToChunk( this.z ) );
		if ( chunk == null ) {
			return 0;
		}
		return chunk.getData( this.x & 0xF, this.y, this.z & 0xF );
	}

	@Override
	public void setBlockId( int id ) {
		final ChunkAdapter chunk = this.world.getChunk( CoordinateUtils.fromBlockToChunk( this.x ), CoordinateUtils.fromBlockToChunk( this.z ) );
		if ( chunk == null ) {
			return;
		}
		chunk.setBlock( this.x & 0xF, this.y, this.z & 0xF, id );
	}

	@Override
	public void setBlockData( byte data ) {
		final ChunkAdapter chunk = this.world.getChunk( CoordinateUtils.fromBlockToChunk( this.x ), CoordinateUtils.fromBlockToChunk( this.z ) );
		if ( chunk == null ) {
			return;
		}
		chunk.setData( this.x & 0xF, this.y, this.z & 0xF, data );
	}
}
