package com.homepanel.core.type;

import com.homepanel.core.state.Type;

public class DefaultBoolean extends Type<Boolean> {

    @Override
    protected String ObjectToString(Boolean input) {

        if (input == null) {
            return "NULL";
        }
        
        return input.toString();
    }

    @Override
    public Boolean convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        return Boolean.valueOf(input);
    }

    @Override
    public String getName() {
        return NAME.BOOLEAN.name();
    }
}