/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.scheduler;

import io.gomint.server.GoMintServer;
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
    private final PriorityQueue<SyncScheduledTask> taskQueue = new PriorityQueue<>( new Comparator<SyncScheduledTask>() {
        @Override
        public int compare( SyncScheduledTask syncScheduledTask1, SyncScheduledTask syncScheduledTask2 ) {
            return (int) ( syncScheduledTask1.getNextExecution() - syncScheduledTask2.getNextExecution() );
        }
    } );

    public void addTask( SyncScheduledTask task ) {
        synchronized ( this.taskQueue ) {
            this.taskQueue.offer( task );
        }
    }

    public void tickTasks() {
        synchronized ( this.taskQueue ) {
            // Iterate over all Tasks until we find some for later ticks
            while ( this.taskQueue.size() > 0 ) {
                SyncScheduledTask task = this.taskQueue.peek();
                if ( task == null || task.getNextExecution() > goMintServer.getCurrentTick() ) {
                    return;
                }

                SyncScheduledTask takeOutTask = this.taskQueue.poll();
                if ( takeOutTask == null || takeOutTask.getNextExecution() > goMintServer.getCurrentTick() ) {
                    // Race condition here
                    return;
                }

                // Check for abort value ( -1 )
                if ( takeOutTask.getNextExecution() == -1 ) {
                    continue;
                }

                takeOutTask.run();

                // Reschedule if needed
                if ( takeOutTask.getNextExecution() > goMintServer.getCurrentTick() ) {
                    this.taskQueue.add( takeOutTask );
                }
            }
        }
    }

    public void removeTask( SyncScheduledTask task ) {
        synchronized ( this.taskQueue ) {
            this.taskQueue.remove( task );
        }
    }
}
