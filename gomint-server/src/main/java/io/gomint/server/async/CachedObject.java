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
public abstract class CachedObject<T> implements FutureListener<Future<T>> {

	private boolean dirty = true;
	protected Future<T> cached;

	public boolean dirty() {
		return this.dirty;
	}

	public void flagDirty() {
		this.dirty = true;
		this.cached = null;
	}

	public Future<T> get() {
		if ( this.dirty && this.cached == null ) {
			this.cached = this.get0();
			this.cached.addFutureListener( this );
		}
		return this.cached;
	}

	public void onFutureResolved( Future<T> future ) {
		if ( future.isSuccess() ) {
			this.dirty = false;
		} else {
			this.dirty = true;
			this.cached = null;
		}
	}

	protected abstract Future<T> get0();
	
}
