/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.MathUtils;
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

    private float pitch;
    private float yaw;
    private float headYaw;

    /**
     * Construct a new packet
     */
    public PacketEntityRelativeMovement() {
        super( Protocol.PACKET_ENTITY_RELATIVE_MOVEMENT );
    }

    @Override
    public void serialize( PacketBuffer buffer, int protocolID ) {
        buffer.writeUnsignedVarLong( this.entityId );

        byte flags = 0;
        if ( this.x != 0 ) {
            flags |= 1;
        }

        if ( this.y != 0 ) {
            flags |= 2;
        }

        if ( this.z != 0 ) {
            flags |= 4;
        }

        if ( this.pitch != 0 ) {
            flags |= 8;
        }

        if ( this.headYaw != 0 ) {
            flags |= 16;
        }

        if ( this.yaw != 0 ) {
            flags |= 32;
        }

        buffer.writeByte( flags );

        if ( this.x != 0 ) {
            buffer.writeSignedVarInt( Float.floatToIntBits( DEFAULT_ADD_VALUE + this.x ) - FLOAT_INT_BITS );
        }

        if ( this.y != 0 ) {
            buffer.writeSignedVarInt( Float.floatToIntBits( DEFAULT_ADD_VALUE + this.y ) - FLOAT_INT_BITS );
        }

        if ( this.z != 0 ) {
            buffer.writeSignedVarInt( Float.floatToIntBits( DEFAULT_ADD_VALUE + this.z ) - FLOAT_INT_BITS );
        }
    }

    @Override
    public void deserialize( PacketBuffer buffer, int protocolID ) {
        this.entityId = buffer.readUnsignedVarLong();
        this.flags = buffer.readByte();

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
            this.pitch = ( buffer.readByte() * 360 ) / 256f;
        }

        if ( ( this.flags & 16 ) != 0 ) {
            this.headYaw = ( buffer.readByte() * 360 ) / 256f;
        }

        if ( ( this.flags & 32 ) != 0 ) {
            this.yaw = ( buffer.readByte() * 360 ) / 256f;
        }
    }

}
