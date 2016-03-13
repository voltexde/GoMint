/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server;

import io.gomint.GoMint;
import io.gomint.plugin.PluginManager;
import io.gomint.server.config.ServerConfig;
import io.gomint.server.network.NetworkManager;
import io.gomint.server.plugin.SimplePluginManager;
import io.gomint.server.report.PerformanceReport;
import io.gomint.server.scheduler.SyncScheduledTask;
import io.gomint.server.scheduler.SyncTaskManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author BlackyPaw
 * @author geNAZt
 * @version 1.1
 */
public class GoMintServer implements GoMint {

	private final Logger logger = LoggerFactory.getLogger( GoMintServer.class );

	// Configuration
	private ServerConfig serverConfig;

	// Networking
	private NetworkManager networkManager;

	// Plugin Management
	private PluginManager pluginManager;

	// Task Scheduling
	@Getter
	private SyncTaskManager syncTaskManager;
	private AtomicBoolean running = new AtomicBoolean( true );
	@Getter
	private long              currentTick;
	@Getter
	private ExecutorService   executorService;
	@Getter
	private PerformanceReport performanceReport;

	/**
	 * Starts the GoMint server
	 * @param args which should have been given over from the static Bootstrap
     */
	public GoMintServer( String[] args ) {
		this.performanceReport = new PerformanceReport();

		// ------------------------------------ //
		// Executor Initialization
		// ------------------------------------ //
		this.executorService = new ThreadPoolExecutor( 0, 512, 60L, TimeUnit.SECONDS, new SynchronousQueue<>() );

		// ------------------------------------ //
		// Configuration Initialization
		// ------------------------------------ //
		this.loadConfig();

		// ------------------------------------ //
		// Scheduler Initialization
		// ------------------------------------ //
		this.syncTaskManager = new SyncTaskManager( this );

		SyncScheduledTask task = new SyncScheduledTask(this.syncTaskManager, new Runnable() {
			@Override
			public void run() {
				System.out.println( "Test 1" );
			}
		}, 200, 200, TimeUnit.MILLISECONDS );
		this.syncTaskManager.addTask( task );
		this.syncTaskManager.removeTask( task );

		this.pluginManager = new SimplePluginManager( this );

		// ------------------------------------ //
		// Networking Initialization
		// ------------------------------------ //

		// Startup the RakNet Natives
		this.networkManager = new NetworkManager( this );
		try {
			this.networkManager.initialize( this.serverConfig.getMaxPlayers(), this.serverConfig.getListener().getIp(), this.serverConfig.getListener().getPort() );

			if ( this.serverConfig.isEnablePacketDumping() ) {
				File dumpDirectory = new File( this.serverConfig.getDumpDirectory() );
				if ( !dumpDirectory.exists() ) {
					if ( !dumpDirectory.mkdirs() ) {
						this.logger.error( "Failed to create dump directory; please double-check your filesystem permissions" );
						return;
					}
				} else if ( !dumpDirectory.isDirectory() ) {
					this.logger.error( "Dump directory path does not point to a valid directory" );
					return;
				}

				this.networkManager.setDumpingEnabled( true );
				this.networkManager.setDumpDirectory( dumpDirectory );
			}
		} catch ( SocketException e ) {
			this.logger.error( "Failed to initialize networking", e );
			return;
		}

		// ------------------------------------ //
		// Main Loop
		// ------------------------------------ //
		// Tick loop
		this.currentTick = 0;
		while ( this.running.get() ) {
			long start = System.currentTimeMillis();

			// Tick the syncTaskManager
			this.syncTaskManager.tickTasks();

			// Tick all major subsystems:
			this.networkManager.tick();

			// Increase the tick
			this.currentTick++;

			long diff = System.currentTimeMillis() - start;
			if ( diff < 50 ) {
				try {
					Thread.sleep( 50 - diff );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
		}
	}

    private void loadConfig() {
        this.serverConfig = new ServerConfig();

        try {
            this.serverConfig.initialize( new File( "server.cfg" ) );
        } catch ( IOException e ) {
            logger.error( "server.cfg is corrupted: ", e );
            System.exit( -1 );
        }

        try ( FileWriter fileWriter = new FileWriter( new File( "server.cfg" ) ) ) {
            this.serverConfig.write( fileWriter );
        } catch ( IOException e ) {
            logger.warn( "Could not save server.cfg: ", e );
        }
    }

    @Override
	public String getMotd() {
		return this.networkManager.getMotd();
	}

	@Override
	public void setMotd( String motd ) {
		this.networkManager.setMotd( motd );
	}
}