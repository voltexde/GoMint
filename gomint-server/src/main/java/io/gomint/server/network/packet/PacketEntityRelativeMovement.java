/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketEntityRelativeMovement extends Packet {

    private static final float DEFAULT_ADD_VALUE = 123f;
    private static final int FLOAT_INT_BITS = Float.floatToIntBits( DEFAULT_ADD_VALUE );

    private long entityId;
    private int flags;

    private float x;
    private float y;
    private float z;

    private byte pitch;
    private byte yaw;
    private byte headYaw;

    /**
     * Construct a new packet
     */
    public PacketEntityRelativeMovement() {
        super( Protocol.PACKET_ENTITY_RELATIVE_MOVEMENT );
    }

    @Override
    public void serialize( PacketBuffer buffer, int protocolID ) {

    }

    @Override
    public void deserialize( PacketBuffer buffer, int protocolID ) {
        this.entityId = buffer.readUnsignedVarLong();
        this.flags = buffer.readLShort();

        if ( ( this.flags & 1 ) != 0 ) {
            this.x = Float.intBitsToFloat( FLOAT_INT_BITS + buffer.readSignedVarInt() ) - DEFAULT_ADD_VALUE;
        }

        if ( ( this.flags & 2 ) != 0 ) {
            this.y = Float.intBitsToFloat( FLOAT_INT_BITS + buffer.readSignedVarInt() ) - DEFAULT_ADD_VALUE;
        }

        if ( ( this.flags & 4 ) != 0 ) {
            this.z = Float.intBitsToFloat( FLOAT_INT_BITS + buffer.readSignedVarInt() ) - DEFAULT_ADD_VALUE;
        }

        if ( ( this.flags & 8 ) != 0 ) {
            this.pitch = buffer.readByte();
        }

        if ( ( this.flags & 16 ) != 0 ) {
            this.yaw = buffer.readByte();
        }

        if ( ( this.flags & 32 ) != 0 ) {
            this.headYaw = buffer.readByte();
        }

        System.out.println( this.flags );
    }

}
