package com.homepanel.core.type;

import com.homepanel.core.state.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DefaultDate extends Type<LocalDate> {

    @Override
    protected String ObjectToString(LocalDate input) {

        if (input == null) {
            return "NULL";
        }

        return DateTimeFormatter.ISO_DATE.format(input);
    }

    @Override
    public LocalDate convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        if (input.length() > 10) {
            input = input.substring(0, 10);
        }

        return LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
    }

    @Override
    public String getName() {
        return NAME.DATE.name();
    }
}