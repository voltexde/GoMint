/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.emulator;

import io.gomint.emulator.client.Client;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.security.Security;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BootStrap {

    public static void main( String[] args ) throws InterruptedException {
        Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider() );

        Configurator.setLevel( "io.gomint.jraknet.ClientSocket", Level.ERROR );

        ScheduledExecutorService service = Executors.newScheduledThreadPool( 256 ); // Amount of cores
        PostProcessExecutorService postProcessExecutorService = new PostProcessExecutorService();

        service.scheduleAtFixedRate( new Runnable() {
            @Override
            public void run() {
                Client.printDebug();
            }
        }, 5, 5, TimeUnit.SECONDS );


        for ( int i = 0; i < 1000; i++ ) {
            service.execute( () -> {
                Client client = new Client( service, postProcessExecutorService.getExecutor(), null );
                client.ping( "", 19132 );

                try {
                    Thread.sleep( 150 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

                client.connect( "", 19132 );
            } );

            Thread.sleep( 1000 );
        }
    }

}
