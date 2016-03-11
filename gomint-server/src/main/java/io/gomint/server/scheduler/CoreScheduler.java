/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.scheduler;

import io.gomint.scheduler.Scheduler;
import io.gomint.scheduler.Task;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Fabian
 * @version 1.0
 */
@RequiredArgsConstructor
public class CoreScheduler implements Scheduler {
    private final ExecutorService executorService;
    private final SyncTaskManager syncTaskManager;

    @Override
    public Task executeAsync( Runnable runnable ) {
        return this.scheduleAsync( runnable, 0, TimeUnit.MILLISECONDS );
    }

    @Override
    public Task scheduleAsync( Runnable runnable, long delay, TimeUnit timeUnit ) {
        return this.scheduleAsync( runnable, delay, delay, timeUnit );
    }

    @Override
    public Task scheduleAsync( Runnable runnable, long delay, long period, TimeUnit timeUnit ) {
        AsyncScheduledTask task = new AsyncScheduledTask( runnable, delay, period, timeUnit );
        executorService.execute( task );
        return task;
    }
}
