package io.gomint.command.validator;

import io.gomint.command.CommandSender;
import io.gomint.command.ParamType;
import io.gomint.command.ParamValidator;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author geNAZt
 * @version 1.0
 */
public class StringValidator extends ParamValidator {

    private final Pattern pattern;

    public StringValidator( String regex ) {
        this.pattern = Pattern.compile( regex );
    }

    @Override
    public ParamType getType() {
        return ParamType.STRING;
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
    public Object validate( String input, CommandSender commandSender ) {
        if ( this.pattern.matcher( input ).matches() ) {
            return input;
        }

        return null;
    }

    @Override
    public String consume( Iterator<String> data ) {
        if ( data.hasNext() ) {
            return data.next();
        }

        return null;
    }

    @Override
    public String getHelpText() {
        return "string:" + this.pattern.pattern();
    }

}
