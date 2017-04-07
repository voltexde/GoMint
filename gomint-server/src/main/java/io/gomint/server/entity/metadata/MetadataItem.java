package io.gomint.server.entity.metadata;

import io.gomint.inventory.ItemStack;
import io.gomint.jraknet.PacketBuffer;

/**
 * @author geNAZt
 * @version 1.0
 */
public class MetadataItem extends MetadataValue {

    private ItemStack value;

    /**
     * Constructs a new metadata item
     */
    public MetadataItem() {

    }

    /**
     * Constructs a new metadata item and initializes it with the specified value.
     *
     * @param value The value to initialize the metadata item with
     */
    public MetadataItem( ItemStack value ) {
        this.value = value;
    }

    /**
     * Gets the value of this metadata item.
     *
     * @return The value of this metadata item
     */
    public ItemStack getValue() {
        return this.value;
    }

    /**
     * Sets the value of this metadata item.
     *
     * @param value The value of this metadata item
     */
    public void setValue( ItemStack value ) {
        this.value = value;
    }

    @Override
    void serialize( PacketBuffer buffer, int index ) {
        super.serialize( buffer, index );
        buffer.writeLShort( this.value.getId() );
        buffer.writeByte( (byte) this.value.getData() );
        buffer.writeLShort( this.value.getAmount() );
    }

    @Override
    void deserialize( PacketBuffer buffer ) {
        this.value = new ItemStack( buffer.readLShort(), buffer.readByte(), buffer.readLShort() );
    }

    @Override
    byte getTypeId() {
        return MetadataContainer.METADATA_ITEM;
    }

}
