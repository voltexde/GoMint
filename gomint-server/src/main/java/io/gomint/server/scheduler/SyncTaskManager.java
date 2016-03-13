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

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class SyncTaskManager {
    @Getter private final GoMintServer goMintServer;
    private final TaskList<SyncScheduledTask> taskList = new TaskList<>();

    /**
     * Add a new pre configured Task to this scheduler
     * @param task which shoould be executed
     */
    public void addTask( SyncScheduledTask task ) {
        if ( task.getNextExecution() == -1 ) return;

        synchronized ( this.taskList ) {
            this.taskList.add( task.getNextExecution(), task );
        }
    }

    /**
     * Tick all tasks
     */
    public void tickTasks() {
        synchronized ( this.taskList ) {
            // Iterate over all Tasks until we find some for later ticks
            while ( !this.taskList.checkNextKey( goMintServer.getCurrentTick() ) ) {
                SyncScheduledTask task = this.taskList.getNextElement();
                if ( task == null || task.getNextExecution() > goMintServer.getCurrentTick() ) {
                    return;
                }

                // Check for abort value ( -1 )
                if ( task.getNextExecution() == -1 ) {
                    continue;
                }

                task.run();

                // Reschedule if needed
                if ( task.getNextExecution() > goMintServer.getCurrentTick() ) {
                    this.taskList.add( task.getNextExecution(), task );
                }
            }
        }
    }

    public void removeTask( SyncScheduledTask task ) {
        synchronized ( this.taskList ) {
            this.taskList.remove( task );
        }
    }
}
