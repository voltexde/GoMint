/*
 * Copyright (c) 2018 Gomint team
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.safeexec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Main {

    private static final AtomicLong PROCESS_ID_COUNTER = new AtomicLong( 0 );

    /**
     * A helper which gets used to start a third party program and correctly close it when the parent process dies
     *
     * @param args arguments given from the parent
     */
    public static void main( String[] args ) {
        // Needed service
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        // Time tracker
        AtomicLong tracker = new AtomicLong( System.currentTimeMillis() );

        // Currently alive processes
        List<ProcessWrapper> childProcesses = new CopyOnWriteArrayList<>();

        // Check if tracker expired
        executorService.scheduleAtFixedRate( () -> {
            long current = System.currentTimeMillis();
            long diff = current - tracker.get();
            if ( diff > TimeUnit.SECONDS.toMillis( 5 ) ) {
                for ( ProcessWrapper wrapper : childProcesses ) {
                    wrapper.kill();
                }
            }

            for ( ProcessWrapper wrapper : childProcesses ) {
                if ( !wrapper.isAlive() ) {
                    childProcesses.remove( wrapper );
                }
            }
        }, 1, 1, TimeUnit.SECONDS );

        // We read stdin and wait for the exec line
        try ( BufferedReader stdin = new BufferedReader( new InputStreamReader( System.in ) ) ) {
            String line;
            while ( ( line = stdin.readLine() ) != null ) {
                if ( line.equals( "ping" ) ) {                                      // Parent wants to update tracker
                    tracker.set( System.currentTimeMillis() );
                } else if ( line.startsWith( "exec" ) ) {                           // Parent wants to execute something
                    // Exec has a work dir and a start cmd
                    // exec w="workdir here" c="cmd here"

                    // Prepare parameter buffers
                    StringBuilder workdir = new StringBuilder();
                    StringBuilder command = new StringBuilder();

                    // Parsing the input
                    boolean escaped = false;
                    boolean inValue = false;

                    StringBuilder current = null;
                    for ( int i = 4; i < line.length(); i++ ) {
                        char c = line.charAt( i );

                        // Check for special chars
                        if ( !inValue ) {
                            // Check for selector
                            if ( c == 'w' ) {
                                current = workdir;
                            } else if ( c == 'c' ) {
                                current = command;
                            } else if ( c == '"' ) {
                                inValue = true;
                            } else if ( c != ' ' && c != '=' ) {
                                System.out.println( c );
                            }
                        } else {
                            if ( escaped ) {
                                current.append( c );
                                escaped = false;
                                continue;
                            }

                            if ( c == '"' ) {
                                inValue = false;
                                current = null;
                            } else if ( c == '\\' ) {
                                escaped = true;
                            } else {
                                current.append( c );
                            }
                        }
                    }

                    ProcessBuilder builder = new ProcessBuilder( command.toString() );
                    builder.directory( new File( workdir.toString() ) );

                    try {
                        ProcessWrapper wrapper = new ProcessWrapper( builder );
                        childProcesses.add( wrapper );
                        System.out.println( "started:" + wrapper.processID );
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                } else if ( line.startsWith( "stop" ) ) {
                    long processId = Long.parseLong( line.split( " " )[1] );
                    for ( ProcessWrapper childProcess : childProcesses ) {
                        if ( childProcess.processID == processId ) {
                            childProcess.kill();
                        }
                    }
                }
            }
        } catch ( IOException e ) {
            System.out.println( "Could not read stdin" );
            e.printStackTrace();
        }
    }

    private static class ProcessWrapper {

        private Process process;
        private final long processID;

        ProcessWrapper( ProcessBuilder builder ) throws IOException {
            this.processID = PROCESS_ID_COUNTER.getAndIncrement();
            this.process = builder.start();

            Thread stdReader = new Thread( () -> {
                try ( BufferedReader in = new BufferedReader( new InputStreamReader( process.getInputStream() ) ) ) {
                    String line;
                    while ( ( line = in.readLine() ) != null ) {
                        System.out.println( this.processID + ": " + line );
                    }
                } catch ( Exception e ) {
                    // Ignored
                }
            } );
            stdReader.setDaemon( true );
            stdReader.start();
        }

        boolean isAlive() {
            return this.process.isAlive();
        }

        void kill() {
            this.process.destroyForcibly();
        }

    }

}
