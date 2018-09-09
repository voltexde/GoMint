package io.gomint.server.world.converter.anvil;

import io.gomint.server.util.Pair;

interface Converter {
    Pair<String, Byte> convert( int blockId, byte metaData );
}
