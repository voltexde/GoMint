package io.gomint.helper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author geNAZt
 * @version 1.0
 */
public class DisassemblyParser {

    @RequiredArgsConstructor
    @ToString
    private static class Packet {
        private final String name;
        private int packetId;
    }

    public static void main( String[] args ) {
        Map<String, Packet> found = new HashMap<>();

        Packet current = null;
        boolean readId = false;
        boolean readPacketContent = false;
        boolean dumpAll = false;

        try ( BufferedReader input = new BufferedReader( new FileReader( new File( "dump.txt" ) ) ) ) {
            String line;
            while ( ( line = input.readLine() ) != null ) {
                // This indicates a method
                if ( line.startsWith( "0" ) ) {
                    // Skip the memory reference
                    String memoryAddress = line.substring( 0, 8 );
                    String modify = line.substring( 10 );
                    modify = modify.substring( 0, modify.length() - 2 );

                    String className = modify.split( "::" )[0];
                    if ( className.endsWith( "Packet" ) && !className.equals( "Packet" ) ) {
                        if ( current == null || !current.name.equals( className ) ) {
                            current = found.computeIfAbsent( className, new Function<String, Packet>() {
                                @Override
                                public Packet apply( String s ) {
                                    return new Packet( className );
                                }
                            } );
                        }

                        if ( modify.contains( "getId()" ) ) {
                            readId = true;
                        } else if ( modify.contains( "read(BinaryStream&)" ) ) {
                            readPacketContent = true;
                        }
                    } else if ( className.equals( "ReadOnlyBinaryStream" ) ) {
                        // System.out.println( line );
                        dumpAll = true;
                    }
                } else if ( readId ) {
                    if ( line.isEmpty() ) {
                        readId = false;
                    } else {
                        if ( line.contains( "movs" ) ) {
                            String id = line.split( "#" )[1].split( ";" )[0].trim();
                            current.packetId = Integer.parseInt( id );
                        }
                    }
                } else if ( readPacketContent ) {
                    if ( line.isEmpty() ) {
                        readPacketContent = false;
                    } else {
                        if ( line.contains( "blx" ) && !line.contains( "r3" ) ) {
                            // System.out.println( current.name + ": " + line );
                        }
                    }
                } else if ( dumpAll ) {
                    if ( line.isEmpty() ) {
                        readPacketContent = false;
                        //System.out.println();
                    } else {
                        //System.out.println( line );
                    }
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        found.forEach( new BiConsumer<String, Packet>() {
            @Override
            public void accept( String s, Packet packet ) {
                System.out.println( s + ": " + packet.packetId );
            }
        } );
    }

}
