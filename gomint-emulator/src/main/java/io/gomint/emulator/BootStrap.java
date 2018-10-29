/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.emulator;

import io.gomint.emulator.client.Client;
import io.gomint.jraknet.ClientSocket;
import io.gomint.jraknet.Socket;
import io.gomint.jraknet.SocketEvent;
import io.gomint.jraknet.SocketEventHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BootStrap {

    public static void main( String[] args ) throws InterruptedException {
        Configurator.setLevel( "io.gomint.jraknet.ClientSocket", Level.ERROR );

        ScheduledExecutorService service = Executors.newScheduledThreadPool( 256 ); // Amount of cores
        PostProcessExecutorService postProcessExecutorService = new PostProcessExecutorService();

        service.scheduleAtFixedRate( Client::printDebug, 5, 5, TimeUnit.SECONDS );

        for ( int i = 0; i < 1; i++ ) {
            service.execute( () -> {
                Client client = new Client( service, postProcessExecutorService.getExecutor(), null );
                client.connect( "192.168.178.113", 19132 );

                ClientSocket socket = new ClientSocket();
                socket.setMojangModificationEnabled( true );
                socket.setEventHandler( new SocketEventHandler() {
                    @Override
                    public void onSocketEvent( Socket socket, SocketEvent socketEvent ) {
                        if ( socketEvent.getType() == SocketEvent.Type.UNCONNECTED_PONG ) {
                            SocketEvent.PingPongInfo info = socketEvent.getPingPongInfo();
                            System.out.println( "Pinged: " + info.getMotd() );
                        }
                    }
                } );
                try {
                    socket.initialize();
                    socket.ping( "localhost", 19132 );
                } catch ( SocketException e ) {
                    e.printStackTrace();
                }
            } );

            Thread.sleep( 1000 );
        }
    }

}
