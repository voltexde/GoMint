/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.scheduler;

import io.gomint.scheduler.Scheduler;
import io.gomint.scheduler.Task;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class CoreScheduler implements Scheduler {

    private final ScheduledExecutorService executorService;
    private final SyncTaskManager syncTaskManager;

    @Override
    public Task executeAsync( Runnable runnable ) {
        return this.scheduleAsync( runnable, 0, TimeUnit.MILLISECONDS );
    }

    @Override
    public Task scheduleAsync( Runnable runnable, long delay, TimeUnit timeUnit ) {
        return this.scheduleAsync( runnable, delay, -1, timeUnit );
    }

    @Override
    public Task scheduleAsync( Runnable runnable, long delay, long period, TimeUnit timeUnit ) {
        AsyncScheduledTask task = new AsyncScheduledTask( runnable );

        Future<?> future;
        if ( period > 0 ) {
            future = this.executorService.scheduleAtFixedRate( task, delay, period, timeUnit );
        } else if ( delay > 0 ) {
            future = this.executorService.schedule( task, delay, timeUnit );
        } else {
            future = this.executorService.submit( task );
        }

        task.setFuture( future );
        return task;
    }

    @Override
    public Task execute( Runnable runnable ) {
        return this.schedule( runnable, 0, TimeUnit.MILLISECONDS );
    }

    @Override
    public Task schedule( Runnable runnable, long delay, TimeUnit timeUnit ) {
        return this.schedule( runnable, delay, -1, timeUnit );
    }

    @Override
    public Task schedule( Runnable runnable, long delay, long period, TimeUnit timeUnit ) {
        SyncScheduledTask task = new SyncScheduledTask( this.syncTaskManager, runnable, delay, period, timeUnit );
        this.syncTaskManager.addTask( task );
        return task;
    }

}
