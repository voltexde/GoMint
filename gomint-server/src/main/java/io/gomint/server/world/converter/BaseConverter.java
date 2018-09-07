package io.gomint.server.world.converter;

import io.gomint.leveldb.DB;
import io.gomint.leveldb.NativeLoader;
import io.gomint.server.util.BlockIdentifier;

import java.io.File;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BaseConverter {

    static {
        NativeLoader.load();
    }

    private DB db;

    public BaseConverter( File worldFolder ) {
        // Create new leveldb
        this.db = new DB( new File( worldFolder, "db" ) );
        this.db.open();
    }

    public void storeSubChunkBlocks( BlockIdentifier[] blocks ) {
        // Currently all blocks of a sub chunk are stored in a single palette

    }

}
