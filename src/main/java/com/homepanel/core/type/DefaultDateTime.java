package com.homepanel.core.type;

import com.homepanel.core.state.Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DefaultDateTime extends Type<LocalDateTime> {

    @Override
    protected String ObjectToString(LocalDateTime input) {

        if (input == null) {
            return "NULL";
        }

        return DateTimeFormatter.ISO_DATE_TIME.format(input);
    }

    @Override
    public LocalDateTime convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        return LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public String getName() {
        return NAME.DATE_TIME.name();
    }
}