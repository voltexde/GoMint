package io.gomint.server.world.generator;

import io.gomint.server.world.ChunkAdapter;
import io.gomint.server.world.WorldAdapter;
import io.gomint.server.world.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ConfigurableLayerGenerator extends ChunkGenerator {

    private List<Byte> layers = new ArrayList<>();
    private List<Byte> dataIds = new ArrayList<>();

    /**
     * Creata a layer chunk generator (flat in MC:PE)
     *
     * @param worldAdapter for which we generate this chunk
     */
    public ConfigurableLayerGenerator( WorldAdapter worldAdapter ) {
        super( worldAdapter );
    }

    /**
     * Add a new layer for generation
     *
     * @param blockId to use in the new layer
     * @param blockData to user in the new layer
     */
    public void addLayer( byte blockId, byte blockData ) {
        this.layers.add( blockId );
        this.dataIds.add( blockData );
    }

    @Override
    public ChunkAdapter generate( int x, int z ) {
        ChunkAdapter chunkAdapter = this.generateEmpty( x, z );

        // Layers are starting from the bottom
        int y = 0;
        for ( byte layer : this.layers ) {
            for ( int xBlock = 0; xBlock < 16; xBlock++ ) {
                for ( int zBlock = 0; zBlock < 16; zBlock++ ) {
                    chunkAdapter.setBlock( xBlock, y, zBlock, layer );
                    chunkAdapter.setData( xBlock, y, zBlock, dataIds.get( y ) );
                }
            }

            y++;
        }

        return chunkAdapter;
    }

}
