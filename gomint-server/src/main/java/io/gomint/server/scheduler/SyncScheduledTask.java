/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.scheduler;

import io.gomint.scheduler.Task;
import io.gomint.util.ExceptionHandler;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SyncScheduledTask implements Task, Runnable {
    private final SyncTaskManager syncTaskManager;
    private final Runnable task;

    private final long period;          // -1 means no reschedule

    @Getter private long nextExecution; // -1 is cancelled

    private ExceptionHandler exceptionHandler;

    /**
     * Constructs a new SyncScheduledTask. It needs to be executed via a normal {@link java.util.concurrent.ExecutorService}
     *
     * @param task runnable which should be executed
     * @param delay of this execution
     * @param period delay after execution to run the runnable again
     * @param unit of time
     */
    public SyncScheduledTask( SyncTaskManager syncTaskManager, Runnable task, long delay, long period, TimeUnit unit ) {
        this.syncTaskManager = syncTaskManager;
        this.task = task;
        this.period = ( period >= 0) ? ( (Double) Math.floor( unit.toMillis( period ) / 50 ) ).longValue() : -1;

        this.nextExecution = ( delay >= 0 ) ? ( syncTaskManager.getGoMintServer().getCurrentTick() + ( (Double) Math.floor( unit.toMillis( delay ) / 50 ) ).longValue() ) : -1;
    }

    @Override
    public void run() {
        try {
            this.task.run();
        } catch ( Exception e ) {
            if ( this.exceptionHandler != null ) {
                if ( !this.exceptionHandler.onException( e ) ) {
                    this.cancel();
                }
            } else {
                e.printStackTrace();
            }
        }

        if ( this.period > 0 ) {
            this.nextExecution = syncTaskManager.getGoMintServer().getCurrentTick() + this.period;
        } else {
            this.cancel();
        }
    }

    @Override
    public void cancel() {
        this.syncTaskManager.removeTask( this );
        this.nextExecution = -1;
    }

    @Override
    public void onException( ExceptionHandler exceptionHandler ) {
        this.exceptionHandler = exceptionHandler;
    }
}
