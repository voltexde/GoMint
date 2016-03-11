/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.event;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;

/**
 * @author BlackyPaw
 * @version 1.0
 */
@EqualsAndHashCode( callSuper = false )
class EventHandlerMethod implements Comparable<EventHandlerMethod> {

	private final EventListener instance;
	private final Method        method;
	private final EventHandler  annotation;

	public EventHandlerMethod( final EventListener instance, final Method method, final EventHandler annotation ) {
		this.instance = instance;
		this.method = method;
		this.annotation = annotation;
	}

	public void invoke( Object[] args ) {
		try {
			this.method.invoke( this.instance, args );
		} catch ( Throwable cause ) {
			cause.printStackTrace();
		}
	}

	public boolean ignoreCancelled() {
		return this.annotation.ignoreCancelled();
	}

	@Override
	public int compareTo( EventHandlerMethod o ) {
		return ( Byte.compare( this.annotation.priority().getValue(), o.annotation.priority().getValue() ) );
	}
}
