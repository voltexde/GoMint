/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.async;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class DelegateCachedObject<T> extends CachedObject<T> {

	private final DelegateR<Future<T>> delegate;

	public DelegateCachedObject( DelegateR<Future<T>> delegate ) {
		this.delegate = delegate;
	}

	@Override
	protected Future<T> get0() {
		return this.delegate.invoke();
	}
}
