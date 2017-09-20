package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import io.gomint.server.network.type.CommandData;

import java.util.List;
import java.util.Map;

/**
 * @author geNAZt
 */
public class PacketAvailableCommands extends Packet {

    private List<String> enumValues;
    private List<String> postFixes;
    private Map<String, List<Integer>> enums;
    private List<CommandData> commandData;

    /**
     * Construct a new packet
     */
    protected PacketAvailableCommands() {
        super( Protocol.PACKET_AVAILABLE_COMMANDS );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        // First we need to write all enum values
        buffer.writeUnsignedVarInt( this.enumValues.size() );
        for ( String enumValue : this.enumValues ) {
            buffer.writeString( enumValue );
        }

        // After that we write all postfix data
        buffer.writeUnsignedVarInt( this.postFixes.size() );
        for ( String postFix : this.postFixes ) {
            buffer.writeString( postFix );
        }

        // Now we need to write enum index value data
        buffer.writeUnsignedVarInt( this.enums.size() );
        for ( Map.Entry<String, List<Integer>> entry : this.enums.entrySet() ) {
            buffer.writeString( entry.getKey() );
            buffer.writeUnsignedVarInt( entry.getValue().size() );
            for ( Integer enumValueIndex : entry.getValue() ) {
                writeEnumIndex( enumValueIndex, buffer );
            }
        }

        // Now write command data
        buffer.writeUnsignedVarInt( this.commandData.size() );
        for ( CommandData data : this.commandData ) {
            buffer.writeString( data.getName() );
            buffer.writeString( data.getDescription() );
        }
    }

    private void writeEnumIndex( int enumValueIndex, PacketBuffer buffer ) {
        if ( this.enumValues.size() < 256 ) {
            buffer.writeByte( (byte) enumValueIndex );
        } else if ( this.enumValues.size() < 65536 ) {
            buffer.writeLShort( (short) enumValueIndex );
        } else {
            buffer.writeLInt( enumValueIndex );
        }
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {

    }

}
