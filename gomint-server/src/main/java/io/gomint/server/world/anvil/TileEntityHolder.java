package io.gomint.server.world.anvil;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
@ToString
public class TileEntityHolder {

    private Map<String, Object> data = new HashMap<>();

    public void add( String key, Object data ) {
        this.data.put( key, data );
    }

    public Object get( String key ) {
        return this.data.get( key );
    }
}
