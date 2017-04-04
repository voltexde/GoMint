/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

import io.gomint.server.scheduler.TaskList;
import junit.framework.Assert;
import org.junit.Test;


/**
 * @author geNAZt
 */
public class TestTaskList {
    @Test
    public void testAdd() {
        TaskList<String> taskList = new TaskList<>();
        taskList.add( 1, "a" );
        taskList.add( 1, "b" );
        taskList.add( 4, "c" );
        taskList.add( 3, "d" );

        Assert.assertEquals( taskList.checkNextKey( 1 ), true );
        Assert.assertEquals( taskList.getNextElement(), "a" );

        Assert.assertEquals( taskList.checkNextKey( 1 ), true );
        Assert.assertEquals( taskList.getNextElement(), "b" );

        Assert.assertEquals( taskList.checkNextKey( 3 ), true );
        Assert.assertEquals( taskList.getNextElement(), "d" );

        Assert.assertEquals( taskList.checkNextKey( 4 ), true );
        Assert.assertEquals( taskList.getNextElement(), "c" );
    }

    @Test
    public void testRemove() {
        TaskList<String> taskList = new TaskList<>();
        taskList.add( 1, "a" );
        taskList.add( 1, "b" );
        taskList.add( 1, "e" );
        taskList.add( 4, "c" );
        taskList.add( 3, "d" );
        taskList.remove( "e" );

        Assert.assertEquals( taskList.checkNextKey( 1 ), true );
        Assert.assertEquals( taskList.getNextElement(), "a" );

        Assert.assertEquals( taskList.checkNextKey( 1 ), true );
        Assert.assertEquals( taskList.getNextElement(), "b" );

        Assert.assertEquals( taskList.checkNextKey( 3 ), true );
        Assert.assertEquals( taskList.getNextElement(), "d" );

        Assert.assertEquals( taskList.checkNextKey( 4 ), true );
        Assert.assertEquals( taskList.getNextElement(), "c" );
    }
}
