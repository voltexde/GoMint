/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import io.gomint.plugin.Plugin;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SecurityManagerCallerDetector implements CallerDetector {

    private final static MySecurityManager mySecurityManager = new MySecurityManager();

    @Override
    public Class<? extends Plugin> getCallerPlugin() {
        return mySecurityManager.getCallerPlugin();
    }

    /**
     * A custom security manager that exposes the getClassContext() information
     */
    static class MySecurityManager extends SecurityManager {
        public Class<? extends Plugin> getCallerPlugin() {
            for ( Class aClass : getClassContext() ) {
                if ( !aClass.equals( Plugin.class ) && Plugin.class.isAssignableFrom( aClass ) ) {
                    return aClass;
                }
            }

            return null;
        }
    }

}
