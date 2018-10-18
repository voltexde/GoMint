package io.gomint.command.validator;

import io.gomint.command.CommandSender;
import io.gomint.command.ParamType;
import io.gomint.command.ParamValidator;
import io.gomint.command.PlayerCommandSender;
import io.gomint.entity.EntityPlayer;
import io.gomint.math.BlockPosition;

import java.util.Iterator;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockPositionValidator extends ParamValidator {

    @Override
    public ParamType getType() {
        return ParamType.BLOCK_POS;
    }

    @Override
    public boolean hasValues() {
        return false;
    }

    @Override
    public List<String> values() {
        return null;
    }

    @Override
    public Object validate( String input, CommandSender sender ) {
        // 0 -> x
        // 1 -> y
        // 2 -> z

        BlockPosition entityPosition = null;
        if ( sender instanceof PlayerCommandSender ) {
            // Mojang decided that ~ is the current entity position
            entityPosition = ( (EntityPlayer) sender ).getLocation().toBlockPosition();
        }

        // Split string
        String[] split = input.split( " " );

        // Parse x
        Integer xInt = parsePos( entityPosition, split[0] );
        if ( xInt == null ) {
            return null;
        }

        Integer yInt = parsePos( entityPosition, split[1] );
        if ( yInt == null ) {
            return null;
        }

        Integer zInt = parsePos( entityPosition, split[2] );
        if ( zInt == null ) {
            return null;
        }

        return new BlockPosition( xInt, yInt, zInt );
    }

    private Integer parsePos( BlockPosition entityPosition, String in ) {
        if ( in.startsWith( "~" ) && entityPosition != null ) {
            int xInt = entityPosition.getX();

            // Do we have additional data (+/-)?
            if ( in.length() > 2 ) {
                if ( in.startsWith( "~+" ) ) {
                    try {
                        int diffX = Integer.parseInt( in.substring( 2 ) );
                        xInt += diffX;
                        return xInt;
                    } catch ( NumberFormatException e ) {
                        return null;
                    }
                } else if ( in.startsWith( "~-" ) ) {
                    try {
                        int diffX = Integer.parseInt( in.substring( 2 ) );
                        xInt -= diffX;
                        return xInt;
                    } catch ( NumberFormatException e ) {
                        return null;
                    }
                }
            }
        } else {
            try {
                return Integer.parseInt( in );
            } catch ( NumberFormatException e ) {
                return null;
            }
        }

        return null;
    }

    @Override
    public String getHelpText() {
        return "blockpos:x y z";
    }

    @Override
    public String consume( Iterator<String> data ) {
        StringBuilder forValidator = new StringBuilder();
        for ( int i = 0; i < 3; i++ ) {
            if ( !data.hasNext() ) {
                return null;
            }

            forValidator.append( data.next() );
        }

        return forValidator.toString();
    }

}
