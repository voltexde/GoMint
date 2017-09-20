package io.gomint.command.validator;

import io.gomint.command.ParamType;
import io.gomint.command.ParamValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 */
public class EnumValidator implements ParamValidator {

    private final List<String> values = new ArrayList<>();

    public EnumValidator( List<String> enumValues ) {
        if ( enumValues != null ) {
            this.values.addAll( enumValues );
        }
    }

    @Override
    public ParamType getType() {
        return ParamType.STRING_ENUM;
    }

    @Override
    public boolean hasValues() {
        return true;
    }

    @Override
    public List<String> values() {
        return this.values;
    }

    @Override
    public boolean validate( String input ) {
        return this.values.contains( input );
    }

}
