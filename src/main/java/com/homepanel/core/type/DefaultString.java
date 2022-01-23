package com.homepanel.core.type;

import com.homepanel.core.state.Type;

public class DefaultString extends Type<String> {

    @Override
    public String ObjectToString(String input) {

        if (input == null || input.isEmpty()) {
            return "NULL";
        }

        return input;
    }

    @Override
    public String convertStringToObject(String input) {

        if (input.equals("NULL") || input.isEmpty()) {
            return null;
        }

        return input;
    }

    @Override
    public String getName() {
        return NAME.STRING.name();
    }
}