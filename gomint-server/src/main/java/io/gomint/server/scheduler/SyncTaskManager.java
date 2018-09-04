/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.scheduler;

import io.gomint.server.GoMintServer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class SyncTaskManager {

    @Getter private final GoMintServer goMintServer;
    private final PriorityQueue<SyncScheduledTaskHolder> taskList = new PriorityQueue<>( Comparator.comparingLong( o -> o.execution ) );

    /**
     * Add a new pre configured Task to this scheduler
     *
     * @param task which should be executed
     */
    public void addTask( SyncScheduledTask task ) {
        if ( task.getNextExecution() == -1 ) return;
        this.taskList.add( new SyncScheduledTaskHolder( task.getNextExecution(), task ) );
    }

    /**
     * Remove a specific task
     *
     * @param task The task which should be removed
     */
    void removeTask( SyncScheduledTask task ) {
        this.taskList.remove( new SyncScheduledTaskHolder( -1, task ) );
    }

    /**
     * Update and run all tasks which should be run
     *
     * @param currentMillis The amount of millis when the update started
     */
    public void update( long currentMillis ) {
        // Iterate over all Tasks until we find some for later ticks
        while ( this.taskList.peek() != null && this.taskList.peek().execution < currentMillis ) {
            SyncScheduledTaskHolder holder = this.taskList.poll();
            if ( holder == null ) {
                return;
            }

            SyncScheduledTask task = holder.task;
            if ( task == null ) {
                return;
            }

            // Check for abort value ( -1 )
            if ( task.getNextExecution() == -1 ) {
                continue;
            }

            task.run();

            // Reschedule if needed
            if ( task.getNextExecution() > currentMillis ) {
                this.taskList.add( new SyncScheduledTaskHolder( task.getNextExecution(), task ) );
            }
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode( of = { "task" } )
    private static class SyncScheduledTaskHolder {
        @Getter
        private long execution;
        private SyncScheduledTask task;
    }

}
