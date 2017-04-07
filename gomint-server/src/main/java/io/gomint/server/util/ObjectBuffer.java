package io.gomint.server.util;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ObjectBuffer<T> {

    private T[] internalBuffer;
    private int elementsIn;
    private int elementsOut;
    private int currentIndex = -1;
    private int initialSize;

    public ObjectBuffer( int initialSize ) {
        this.internalBuffer = (T[]) new Object[initialSize];
        this.initialSize = initialSize;
    }

    public synchronized T get() {
        this.elementsOut++;

        // Fast out when buffer is empty
        if ( this.currentIndex == -1 ) {
            return null;
        }

        // Get the next free element
        return this.internalBuffer[this.currentIndex--];
    }

    public synchronized void push( T elem ) {
        // Is buffer full? When it is we don't need this object
        if ( this.internalBuffer.length - 1 == this.currentIndex ) {
            return;
        }

        this.internalBuffer[++this.currentIndex] = elem;
        this.elementsIn++;
    }

    public synchronized void recalc() {
        double diff = this.elementsOut / (double) this.elementsIn;
        if ( diff >= 1.4 ) {
            // Maybe we need to raise this
            if ( this.internalBuffer.length < 500000 ) {
                T[] newBuffer = (T[]) new Object[this.internalBuffer.length * 2];
                System.arraycopy( this.internalBuffer, 0, newBuffer, 0, this.internalBuffer.length );
                this.internalBuffer = newBuffer;
            }
        } else if ( diff < 0.4 ) {
            if ( this.internalBuffer.length > this.initialSize ) {
                T[] newBuffer = (T[]) new Object[this.internalBuffer.length / 2];
                System.arraycopy( this.internalBuffer, 0, newBuffer, 0, newBuffer.length );
                this.internalBuffer = newBuffer;
            }
        }

        this.elementsIn = this.elementsOut = 0;
    }

}
