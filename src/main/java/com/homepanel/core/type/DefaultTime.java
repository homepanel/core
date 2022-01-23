package com.homepanel.core.type;

import com.homepanel.core.state.Type;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DefaultTime extends Type<LocalTime> {

    @Override
    protected String ObjectToString(LocalTime input) {

        if (input == null) {
            return "NULL";
        }

        return DateTimeFormatter.ISO_TIME.format(input);
    }

    @Override
    public LocalTime convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        if (input.length() > 8) {
            input = input.substring(input.length() - 8);
        }

        return LocalTime.parse(input, DateTimeFormatter.ISO_TIME);
    }

    @Override
    public String getName() {
        return NAME.TIME.name();
    }
}