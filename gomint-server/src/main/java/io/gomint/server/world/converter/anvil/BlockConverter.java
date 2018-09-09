package io.gomint.server.world.converter.anvil;

import io.gomint.server.util.Pair;
import io.gomint.taglib.NBTTagCompound;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockConverter {

    private final Short2ObjectMap<Converter> converters = new Short2ObjectOpenHashMap<>();
    private final Int2ByteMap metaconverts = new Int2ByteOpenHashMap();

    public BlockConverter( List<NBTTagCompound> converterData ) {
        for ( NBTTagCompound compound : converterData ) {
            short oldId = compound.getShort( "oi", (short) 0 );
            byte oldMeta = compound.getByte( "om", (byte) -1 );
            String newId = compound.getString( "ni", null );
            byte newMeta = compound.getByte( "nm", (byte) -1 );

            if ( oldMeta == -1 && newMeta == -1 ) {
                this.converters.put( oldId, ( blockId, metaData ) -> new Pair<>( newId, metaData ) );
            } else {
                // Check if we already have a converter
                if ( !this.converters.containsKey( oldId ) ) {
                    this.converters.put( oldId, ( blockId, metaData ) -> new Pair<>( newId, metaconverts.get( blockId << 16 | metaData ) ) );
                }

                this.metaconverts.put( oldId << 16 | oldMeta, newMeta );
            }
        }
    }

    public Pair<String, Byte> convert( short blockId, byte data ) {
        Converter converter = this.converters.get( blockId );
        if ( converter != null ) {
            return converter.convert( blockId, data );
        }

        return null;
    }

}
