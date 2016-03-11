/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.plugin;

import io.gomint.plugin.Plugin;
import io.gomint.plugin.PluginManager;
import io.gomint.server.GoMintServer;
import io.gomint.server.scheduler.CoreScheduler;
import io.gomint.server.util.CallerDetectorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabian
 * @version 1.0
 */
public class SimplePluginManager implements PluginManager {
    private final Logger logger = LoggerFactory.getLogger( SimplePluginManager.class );
    private final CoreScheduler scheduler;

    public SimplePluginManager( GoMintServer server ) {
        this.scheduler = new CoreScheduler( server.getExecutorService(), server.getSyncTaskManager() );
    }

    @Override
    public void disablePlugin( Plugin plugin ) {
        // Check for security
        if ( !CallerDetectorUtil.getCallerClassName( 2 ).equals( plugin.getClass().getName() ) ) {
            throw new SecurityException( "Plugins can only disable themselves" );
        }
    }

    @Override
    public String getBaseDirectory() {
        return null;
    }
}
