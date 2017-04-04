/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.util.LongList;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TickList {

    private LongElement head;

    /**
     * Add a new Element to the tasklist
     *
     * @param key     which should be used to sort the element
     * @param element which should be stored
     */
    public synchronized void add( long key, long element ) {
        // Check if we have a head state
        if ( this.head == null ) {
            this.head = new LongElement( key, null, new LongList() {{
                add( element );
            }} );
        } else {
            LongElement longElement = this.head;
            LongElement previousLongElement = null;

            // Check until we got a element with a key higher than us or we reached the end
            while ( longElement != null && longElement.getKey() < key ) {
                previousLongElement = longElement;
                longElement = longElement.getNext();
            }

            // We are at the end of the chain
            if ( longElement == null ) {
                previousLongElement.setNext( new LongElement( key, null, new LongList() {{
                    add( element );
                }} ) );
            } else {
                // Check if we need to insert a element
                if ( longElement.getKey() != key ) {
                    LongElement newLongElement = new LongElement( key, longElement, new LongList() {{
                        add( element );
                    }} );

                    if ( previousLongElement != null ) {
                        previousLongElement.setNext( newLongElement );
                    } else {
                        // We added a new head
                        this.head = newLongElement;
                    }
                } else {
                    // We already have this key, append task
                    longElement.getValues().add( element );
                }
            }
        }
    }

    /**
     * @return
     */
    public synchronized long getNextTaskTime() {
        return this.head != null ? this.head.getKey() : Long.MAX_VALUE;
    }

    /**
     * Check if the current head key is the key we want to check against
     *
     * @param key to check against
     * @return true when the next key is the key given, false when not
     */
    public synchronized boolean checkNextKey( long key ) {
        return this.head != null && this.head.getKey() == key && this.head.getValues().size() > 0;
    }

    /**
     * Gets the next element in this List. The Element will be removed from the list
     *
     * @return next element out of this list or null when there is none
     */
    public synchronized long getNextElement() {
        // There is nothing we can reach
        if ( this.head == null ) return Long.MIN_VALUE;

        // Check if we have a head node
        while ( this.head != null && this.head.getValues().size() == 0 ) {
            // This head is empty, remove it
            this.head = this.head.getNext();
        }

        // This list has reached its end
        if ( this.head == null ) return Long.MIN_VALUE;

        // Extract the element
        long element = this.head.getValues().remove();
        while ( this.head.getValues().size() == 0 ) {
            this.head = this.head.getNext();
            if ( this.head == null ) break;
        }

        return element;
    }

    @AllArgsConstructor
    @Data
    private final class LongElement {
        private long key;
        private LongElement next;
        private LongList values;
    }

}
