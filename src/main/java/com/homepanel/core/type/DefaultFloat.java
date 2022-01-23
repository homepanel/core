package com.homepanel.core.type;

import com.homepanel.core.state.Type;

public class DefaultFloat extends Type<Number> {

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

        return Float.valueOf(input);
    }

    @Override
    public String getName() {
        return NAME.FLOAT.name();
    }
}