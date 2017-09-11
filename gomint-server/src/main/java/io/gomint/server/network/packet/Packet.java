/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.network.packet;

import io.gomint.inventory.item.ItemAir;
import io.gomint.inventory.item.ItemStack;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.BlockPosition;
import io.gomint.server.entity.EntityLink;
import io.gomint.server.inventory.item.Items;
import io.gomint.taglib.NBTTagCompound;
import io.gomint.world.Gamerule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class Packet {

    private static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger( "FFFFFFFFFFFFFFFF", 16 );

    /**
     * Internal MC:PE id of this packet
     */
    protected final byte id;

    /**
     * Construct a new packet
     *
     * @param id of the packet
     */
    protected Packet( byte id ) {
        this.id = id;
    }

    /**
     * Gets the packet's ID.
     *
     * @return The packet's ID
     */
    public byte getId() {
        return this.id;
    }

    /**
     * Serializes this packet into the given buffer.
     *
     * @param buffer The buffer to serialize this packet into
     */
    public abstract void serialize( PacketBuffer buffer );

    /**
     * Deserializes this packet from the given buffer.
     *
     * @param buffer The buffer to deserialize this packet from
     */
    public abstract void deserialize( PacketBuffer buffer );

    /**
     * Returns an estimate length of the packet (used for pre-allocation).
     *
     * @return The estimate length of the packet or -1 if unknown
     */
    public int estimateLength() {
        return -1;
    }

    /**
     * Returns the ordering channel to send the packet on.
     *
     * @return The ordering channel of the packet
     */
    public int orderingChannel() {
        return 0;
    }

    /**
     * Read a item stack from the packet buffer
     *
     * @param buffer from the packet
     * @return read item stack
     */
    static ItemStack readItemStack( PacketBuffer buffer ) {
        int id = buffer.readSignedVarInt();
        if ( id == 0 ) {
            return ItemAir.create( 0 );
        }

        int temp = buffer.readSignedVarInt();
        byte amount = (byte) ( temp & 0xFF );
        short data = (short) ( temp >> 8 );

        NBTTagCompound nbt = null;
        short extraLen = buffer.readLShort();
        if ( extraLen > 0 ) {
            ByteArrayInputStream bin = new ByteArrayInputStream( buffer.getBuffer(), buffer.getPosition(), extraLen );
            try {
                nbt = NBTTagCompound.readFrom( bin, false, ByteOrder.LITTLE_ENDIAN );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            buffer.skip( extraLen );
        }

        // They implemented additional data for item stacks aside from nbt
        int countPlacedOn = buffer.readSignedVarInt();
        for ( int i = 0; i < countPlacedOn; i++ ) {
            buffer.readString();    // TODO: Implement proper support once we know the string values
        }

        int countCanBreak = buffer.readSignedVarInt();
        for ( int i = 0; i < countCanBreak; i++ ) {
            buffer.readString();    // TODO: Implement proper support once we know the string values
        }

        return Items.create( id, data, amount, nbt );
    }

    /**
     * Write a item stack to the packet buffer
     *
     * @param itemStack which should be written
     * @param buffer    which should be used to write to
     */
    public static void writeItemStack( ItemStack itemStack, PacketBuffer buffer ) {
        if ( itemStack == null || itemStack instanceof ItemAir ) {
            buffer.writeSignedVarInt( 0 );
            return;
        }

        buffer.writeSignedVarInt( ( (io.gomint.server.inventory.item.ItemStack) itemStack ).getMaterial() );
        buffer.writeSignedVarInt( ( itemStack.getData() << 8 ) + ( itemStack.getAmount() & 0xff ) );

        NBTTagCompound compound = itemStack.getNbtData();
        if ( compound == null ) {
            buffer.writeLShort( (short) 0 );
        } else {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                compound.writeTo( byteArrayOutputStream, false, ByteOrder.LITTLE_ENDIAN );
                buffer.writeLShort( (short) byteArrayOutputStream.size() );
                buffer.writeBytes( byteArrayOutputStream.toByteArray() );
            } catch ( IOException e ) {
                e.printStackTrace();
                buffer.writeLShort( (short) 0 );
            }
        }

        // canPlace and canBreak
        buffer.writeSignedVarInt( 0 );
        buffer.writeSignedVarInt( 0 );
    }

    /**
     * Predict the size of an item stack
     *
     * @param itemStack which should be predicted in size
     * @return amount of bytes needed to write given itemstack
     */
    int predictItemStack( ItemStack itemStack ) {
        if ( itemStack == null || itemStack instanceof ItemAir ) {
            return predictSignedVarInt( 0 );
        }

        int idSize = predictSignedVarInt( ( (io.gomint.server.inventory.item.ItemStack) itemStack ).getMaterial() );
        int dataSize = predictSignedVarInt( ( itemStack.getData() << 8 ) + ( itemStack.getAmount() & 0xff ) );

        NBTTagCompound compound = itemStack.getNbtData();
        if ( compound == null ) {
            return idSize + dataSize + 2;
        } else {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                compound.writeTo( byteArrayOutputStream, false, ByteOrder.LITTLE_ENDIAN );

                return idSize + dataSize + 2 + byteArrayOutputStream.size();
            } catch ( IOException e ) {
                return idSize + dataSize + 2;
            }
        }
    }

    /**
     * Write a array of item stacks to the buffer
     *
     * @param itemStacks which should be written to the buffer
     * @param buffer     which should be written to
     */
    void writeItemStacks( ItemStack[] itemStacks, PacketBuffer buffer ) {
        if ( itemStacks == null || itemStacks.length == 0 ) {
            buffer.writeUnsignedVarInt( 0 );
            return;
        }

        buffer.writeUnsignedVarInt( itemStacks.length );

        for ( ItemStack itemStack : itemStacks ) {
            writeItemStack( itemStack, buffer );
        }
    }

    /**
     * Read in a variable amount of itemstacks
     *
     * @param buffer The buffer to read from
     * @return a list of item stacks
     */
    ItemStack[] readItemStacks( PacketBuffer buffer ) {
        int count = buffer.readUnsignedVarInt();
        ItemStack[] itemStacks = new ItemStack[count];

        for ( int i = 0; i < count; i++ ) {
            itemStacks[i] = readItemStack( buffer );
        }

        return itemStacks;
    }

    /**
     * Predict the size of an item stack array
     *
     * @param v array of item stacks to get the size for
     * @return size of item stack array
     */
    int predictItemStacksSize( ItemStack[] v ) {
        int size = predictVarIntSize( v.length );
        for ( ItemStack itemStack : v ) {
            size += predictItemStack( itemStack );
        }

        return size;
    }


    /**
     * Write a array of integers to the buffer
     *
     * @param integers which should be written to the buffer
     * @param buffer   which should be written to
     */
    void writeIntList( int[] integers, PacketBuffer buffer ) {
        if ( integers == null || integers.length == 0 ) {
            buffer.writeUnsignedVarInt( 0 );
            return;
        }

        buffer.writeUnsignedVarInt( integers.length );

        for ( Integer integer : integers ) {
            buffer.writeSignedVarInt( integer );
        }
    }


    /**
     * Predict the byte size of a variable signed integer
     *
     * @param v number which should be predicted
     * @return size in bytes
     */
    int predictSignedVarInt( int v ) {
        long val = (long) ( v << 1 ^ v >> 31 );
        return predictVarLongSize( val );
    }

    /**
     * Predict the byte size of the variable number representation
     *
     * @param input the number to predict
     * @return the amount of bytes
     */
    int predictVarIntSize( int input ) {
        int size = 1;
        int value = input;

        while ( ( value & -128 ) != 0 ) {
            value >>>= 7;
            size++;
        }

        return size;
    }

    /**
     * Predict the byte size of the variable number representation
     *
     * @param input the number to predict
     * @return the amount of bytes
     */
    int predictVarLongSize( long input ) {
        int size = 1;
        long value = input;

        while ( ( value & -128 ) != 0 ) {
            value >>>= 7;
            size++;
        }

        return size;
    }

    /**
     * Predict the byte size of an variable long
     *
     * @param input which should be predicted in size
     * @return size in bytes
     */
    int predictSignedVarLong( long input ) {
        BigInteger origin = BigInteger.valueOf( input );
        BigInteger left = origin.shiftLeft( 1 );
        BigInteger right = origin.shiftRight( 63 );
        BigInteger val = left.xor( right );

        if ( val.compareTo( UNSIGNED_LONG_MAX_VALUE ) > 0 ) {
            throw new IllegalArgumentException( "The value is too big" );
        } else {
            int size = 1;

            val = val.and( UNSIGNED_LONG_MAX_VALUE );
            BigInteger i = BigInteger.valueOf( -128L );

            for ( ; !val.and( i ).equals( BigInteger.ZERO ); val = val.shiftRight( 7 ) ) {
                size++;
            }

            return size + 1;
        }
    }

    public void writeGamerules( Map<Gamerule, Object> gamerules, PacketBuffer buffer ) {
        if ( gamerules == null ) {
            buffer.writeUnsignedVarInt( 0 );
            return;
        }

        buffer.writeUnsignedVarInt( gamerules.size() );
        gamerules.forEach( new BiConsumer<Gamerule, Object>() {
            @Override
            public void accept( Gamerule gamerule, Object value ) {
                buffer.writeString( gamerule.getNbtName().toLowerCase() );

                if ( gamerule.getValueType() == Boolean.class ) {
                    buffer.writeByte( (byte) 1 );
                    buffer.writeBoolean( (Boolean) value );
                } else if ( gamerule.getValueType() == Integer.class ) {
                    buffer.writeByte( (byte) 2 );
                    buffer.writeUnsignedVarInt( (Integer) value );
                } else if ( gamerule.getValueType() == Float.class ) {
                    buffer.writeByte( (byte) 3 );
                    buffer.writeLFloat( (Float) value );
                }
            }
        } );
    }

    public BlockPosition readBlockPosition( PacketBuffer buffer ) {
        return new BlockPosition( buffer.readSignedVarInt(), buffer.readUnsignedVarInt(), buffer.readSignedVarInt() );
    }

    public void writeBlockPosition( BlockPosition position, PacketBuffer buffer ) {
        buffer.writeSignedVarInt( position.getX() );
        buffer.writeUnsignedVarInt( position.getY() );
        buffer.writeSignedVarInt( position.getZ() );
    }

    public int predictString( String v ) {
        byte[] data = v.getBytes( StandardCharsets.UTF_8 );
        int size = predictVarIntSize( data.length );
        return size + data.length;
    }

    public void writeEntityLinks( List<EntityLink> links, PacketBuffer buffer ) {
        if ( links == null ) {
            buffer.writeUnsignedVarInt( 0 );
        } else {
            buffer.writeUnsignedVarInt( links.size() );
            for ( EntityLink link : links ) {
                buffer.writeUnsignedVarLong( link.getFrom() );
                buffer.writeUnsignedVarLong( link.getTo() );
                buffer.writeByte( link.getUnknown1() );
                buffer.writeByte( link.getUnknown2() );
            }
        }
    }

}
