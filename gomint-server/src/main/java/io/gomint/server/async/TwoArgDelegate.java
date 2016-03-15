/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.async;

/**
 * @author geNAZt
 * @version 1.0
 *
 * @param <T1> type of first argument for invoke
 * @param <T2> type of second argument for invoke
 *
 * Delegate which can handle two arguments on invoke
 */
public interface TwoArgDelegate<T1, T2> {

    /**
     * Invoke this Delegate
     *
     * @param arg1 first argument for the delegate
     * @param arg2 second argument for the delegate
     */
	void invoke( T1 arg1, T2 arg2 );

}
