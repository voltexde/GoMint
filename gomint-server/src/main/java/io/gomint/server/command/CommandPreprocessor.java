package io.gomint.server.command;

import io.gomint.command.ParamValidator;
import io.gomint.server.util.IndexedHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author geNAZt
 */
public class CommandPreprocessor {

    // Enums are stored in an indexed list at the start. Enums are just collections of a name and
    // a integer list reflecting the index inside enumValues
    private List<String> enumValues = new ArrayList<>();
    private IndexedHashMap<String, List<Integer>> enums = new IndexedHashMap<>();
    private Map<CommandHolder, Integer> aliasIndex = new HashMap<>();
    private Map<String, Integer> enumIndexes = new HashMap<>();

    // Cached commands packet
    private Packet

    public CommandPreprocessor( List<CommandHolder> commands ) {
        // First we should scan all commands for aliases
        for ( CommandHolder command : commands ) {
            if ( command.getAlias() != null ) {
                for ( String s : command.getAlias() ) {
                    this.addEnum( command.getName() + "CommandAlias", s );
                }

                this.aliasIndex.put( command, this.enums.getIndex( command.getName() + "CommandAlias" ) );
            }
        }

        // Now we need to search for enum validators
        for ( CommandHolder command : commands ) {
            if ( command.getValidators() != null ) {
                for ( Map.Entry<String, ParamValidator> entry : command.getValidators().entrySet() ) {
                    if ( entry.getValue().hasValues() ) {
                        for ( String s : entry.getValue().values() ) {
                            this.addEnum( entry.getKey(), s );
                        }

                        this.enumIndexes.put( entry.getKey(), this.enums.getIndex( entry.getKey() ) );
                    }
                }
            }
        }

        // Now we should have sorted any enums. Move on to write the command data

    }

    private void addEnum( String name, String value ) {
        // Check if we already know this enum value
        int enumValueIndex;
        if ( this.enumValues.contains( value ) ) {
            enumValueIndex = this.enumValues.indexOf( value );
        } else {
            this.enumValues.add( value );
            enumValueIndex = this.enumValues.indexOf( value );
        }

        // Create / add this value to the enum
        this.enums.computeIfAbsent( name, new Function<String, List<Integer>>() {
            @Override
            public List<Integer> apply( String s ) {
                return new ArrayList<>();
            }
        } ).add( enumValueIndex );
    }

}
