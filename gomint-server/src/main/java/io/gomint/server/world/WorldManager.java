/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.server.GoMintServer;
import io.gomint.server.world.anvil.AnvilWorldAdapter;
import net.openhft.koloboke.collect.map.ObjObjCursor;
import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class WorldManager {
    private static final Logger logger = LoggerFactory.getLogger( WorldManager.class );
	private final GoMintServer server;
	private ObjObjMap<String, WorldAdapter> loadedWorlds;

	/**
	 * Constructs a new world manager that does not yet hold any worlds.
	 */
	public WorldManager( GoMintServer server ) {
		this.server = server;
		this.loadedWorlds = HashObjObjMaps.newMutableMap( 4 );
	}

	/**
	 * Ticks all worlds that are currently loaded.
	 */
	public void tick() {
        for ( WorldAdapter worldAdapter : getWorlds() ) {
            worldAdapter.tick();
        }
    }

	/**
	 * Gets a collection of all worlds held by the world manager.
	 *
	 * @return A collection of all worlds held by the world manager
	 */
	public Collection<WorldAdapter> getWorlds() {
		return this.loadedWorlds.values();
	}

	/**
	 * Gets the world with the given name out of the world manager if any such
	 * world exists.
	 *
	 * @param name The name of the world to be retrieved
	 * @return The world if found or null otherwise
	 */
	public WorldAdapter getWorld( String name ) {
		return this.loadedWorlds.get( name );
	}

	/**
	 * Adds the given world to the world manager. It may be retrieved by name
	 * from now on.
	 *
	 * @param world The world to be added
	 */
	public void addWorld( WorldAdapter world ) {
		this.loadedWorlds.put( world.getWorldName(), world );
	}

	/**
	 * Loads the world from the specified path. This method will attempt to detect the
	 * format of the world automatically. On success the world will be added to the
	 * world manage immediately.
	 *
	 * @param path The path of the world
	 * @throws IOException Thrown in case the world could not be loaded
	 */
	public void loadWorld( String path ) throws IOException {
		logger.info( "Attempting to load world '" + path + "'" );

		File file = new File( path );
		if ( !file.exists() ) {
			throw new IOException( "World does not exist" );
		}

		if ( file.isDirectory() ) {
			// Anvil world:
			File levelDat = new File( file, "level.dat" );
			if ( levelDat.exists() && levelDat.isFile() ) {
				logger.info( "Detected anvil world '" + path + "'" );
				this.loadAnvilWorld( file );
				return;
			}
		}

		throw new IOException( "Could not detect world format" );
	}

	/**
	 * Attempts to load an anvil world.
	 *
	 * @param path The path to the world's folder
	 * @throws IOException Thrown in case the world could not be loaded
	 */
	private void loadAnvilWorld( File path ) throws IOException {
		AnvilWorldAdapter world = AnvilWorldAdapter.load( this.server, path );
		this.addWorld( world );
		logger.info( "Successfully loaded world '" + path.getName() + "'" );
	}

}
