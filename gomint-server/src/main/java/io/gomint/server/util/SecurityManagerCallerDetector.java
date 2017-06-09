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

    private static final MySecurityManager SECURITY_MANAGER = new MySecurityManager();

    @Override
    public Class<? extends Plugin> getCallerPlugin() {
        return SECURITY_MANAGER.getCallerPlugin();
    }

    /**
     * A custom security manager that exposes the getClassContext() information
     */
    static class MySecurityManager extends SecurityManager {
        /**
         * Get the plugin who called
         *
         * @return class of the plugin who called
         */
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
