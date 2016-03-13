/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.config;

import com.blackypaw.simpleconfig.SimpleConfig;
import com.blackypaw.simpleconfig.annotation.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BlackyPaw
 * @author geNAZt
 * @version 1.0
 */
@NoArgsConstructor
@Getter
public class ServerConfig extends SimpleConfig {

	// ------------------------ General
	@Comment( "The host and port to bind the server to" )
	private ListenerConfig listener = new ListenerConfig();

	@Comment( "The maximum number of players to play on this server" )
    private int maxPlayers = 10;

	// ------------------------ Packet Dumping
	@Comment( "Enables packet dumping for development purposes; not to be used for production" )
    private boolean enablePacketDumping = false;

	@Comment( "The directory to save packet dumps into if packet dumping is enabled" )
    private String dumpDirectory = "dumps";

	// ------------------------ World
	@Comment( "Name of the world to load on startup" )
	private String world = "world";

	@Comment("Amount of Chunks which will always be loaded and stay loaded around the spawn area.\n" +
	         "You can set this to 0 if you don't want to hold any Chunks in Memory but this also means\n" +
	         "that you have to load the Chunks from disk everytime someone joins and the Chunk GC has cleared\n" +
	         "the chunks. USE 0 WITH CAUTION!!!")
	private int amountOfChunksForSpawnArea = 16;
}
