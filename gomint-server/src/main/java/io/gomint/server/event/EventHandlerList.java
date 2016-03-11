/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.event;

import io.gomint.event.CancelableEvent;
import io.gomint.event.Event;
import io.gomint.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class EventHandlerList {

	// If the handler list is dirty we need to sort it by the event handler priorities:
	private boolean dirty;
	private List<EventHandlerMethod> handlers = new ArrayList<>();

	public EventHandlerList() {

	}

	public void addHandler( EventHandlerMethod handler ) {
		this.handlers.add( handler );
		this.dirty = true;
	}

	public void removeHandler( EventHandlerMethod handler ) {
		this.handlers.remove( handler );
		// Removing does not actually destroy the list's order
	}

	public void triggerEvent( Event event ) {
		if ( this.dirty ) {
			Collections.sort( this.handlers );
		}

		if ( event instanceof CancelableEvent ) {
			CancelableEvent cancelableEvent = (CancelableEvent) event;
			Object[] args = new Object[] { event };
			for ( EventHandlerMethod handler : this.handlers ) {
				if ( cancelableEvent.isCancelled() && handler.ignoreCancelled() ) {
					continue;
				}
				handler.invoke( args );
			}
		} else {
			Object[] args = new Object[] { event };
			for ( EventHandlerMethod handler : this.handlers ) {
				handler.invoke( args );
			}
		}
	}

}
