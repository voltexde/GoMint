/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import com.koloboke.collect.LongCursor;
import com.koloboke.function.LongLongConsumer;
import io.gomint.server.GoMintServer;
import io.gomint.server.util.collection.GUIDSet;
import io.gomint.server.util.collection.WatchdogMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Watchdog {

    private static final Logger LOGGER = LoggerFactory.getLogger( Watchdog.class );
    private final WatchdogMap watchdogMap;

    public Watchdog( GoMintServer server ) {
        this.watchdogMap = WatchdogMap.withExpectedSize( 6 );

        server.getExecutorService().submit( () -> {
            while ( server.isRunning() ) {
                check();

                try {
                    Thread.sleep( 10 );
                } catch ( InterruptedException e ) {
                    // Ignored .-.
                }
            }
        } );
    }

    private synchronized void check() {
        long currentTime = System.currentTimeMillis();

        final GUIDSet[] removeSet = { null };
        this.watchdogMap.forEach( ( threadId, endTime ) -> {
            // Check if we are over endTime
            if ( currentTime > endTime ) {
                // Get the threads stack
                ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                ThreadInfo threadInfo = threadMXBean.getThreadInfo( threadId, 10 );

                LOGGER.warn( "Thread did not work in time: {} (#{})", threadInfo.getThreadName(), threadInfo.getThreadId() );
                LOGGER.warn( "Status: {}", threadInfo.getThreadState() );
                for ( StackTraceElement element : threadInfo.getStackTrace() ) {
                    LOGGER.warn( "  {}", element );
                }

                if ( removeSet[0] == null ) {
                    removeSet[0] = GUIDSet.withExpectedSize( 1 );
                }

                removeSet[0].add( threadId );
            }
        } );

        if ( removeSet[0] != null ) {
            LongCursor cursors = removeSet[0].cursor();
            while ( cursors.moveNext() ) {
                this.watchdogMap.justRemove( cursors.elem() );
            }
        }
    }

    public synchronized void add( long diff, TimeUnit unit ) {
        long currentTime = System.currentTimeMillis();
        this.watchdogMap.justPut( Thread.currentThread().getId(), currentTime + unit.toMillis( diff ) );
    }

    public synchronized void done() {
        this.watchdogMap.justRemove( Thread.currentThread().getId() );
    }

}
