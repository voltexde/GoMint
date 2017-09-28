package io.gomint.server.world;

/**
 * @author geNAZt
 * @version 1.0
 */
public class WorldLoadException extends Exception {

    /**
     * Create a new world load exception with a reason
     *
     * @param message which should be printed as the reason
     */
    public WorldLoadException( String message ) {
        super( message );
    }

}
