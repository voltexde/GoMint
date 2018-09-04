package io.gomint.server.util;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Values {

    public static final float CLIENT_TICK_RATE = TimeUnit.MILLISECONDS.toNanos( 50 ) / (float) TimeUnit.SECONDS.toNanos( 1 );

}
