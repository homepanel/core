package com.homepanel.core.type;

import com.homepanel.core.state.Type;

public class DefaultInteger extends Type<Number> {

    @Override
    protected String ObjectToString(Number input) {

        if (input == null) {
            return "NULL";
        }

        return input.toString();
    }

    @Override
    public Number convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        return Integer.valueOf(input);
    }

    @Override
    public String getName() {
        return NAME.INTEGER.name();
    }
}