/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import io.gomint.server.player.PlayerSkin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @author BlackyPaw
 * @version 1.0
 */
@Data
@EqualsAndHashCode( callSuper = true )
public class PacketLogin extends Packet {

	private String username;
	private int    serverProtocol;
	private int    clientProtocol;
	private long   clientId;
	private UUID   clientUUID;
	private String serverAddress;
	private String clientSecret;
	private String skinName;
	private int    skinLength;

	public PacketLogin() {
		super( Protocol.LOGIN_PACKET );
	}

	@Override
	public void serialize( PacketBuffer buffer ) {
		buffer.writeString( this.username );
		buffer.writeInt( this.serverProtocol );
		buffer.writeInt( this.clientProtocol );
		buffer.writeLong( this.clientId );
		buffer.writeUUID( this.clientUUID );
		buffer.writeString( this.serverAddress );
		buffer.writeString( this.clientSecret );
		buffer.writeString( this.skinName );
		buffer.writeUShort( this.skinLength );
	}

	@Override
	public void deserialize( PacketBuffer buffer ) {
		this.username = buffer.readString();
		this.serverProtocol = buffer.readInt();
		this.clientProtocol = buffer.readInt();
		this.clientId = buffer.readLong();
		this.clientUUID = buffer.readUUID();
		this.serverAddress = buffer.readString();
		this.clientSecret = buffer.readString();
		this.skinName    = buffer.readString();
		this.skinLength  = buffer.readUShort();
	}
}
