package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public abstract class DefaultDimmer<T> extends Type {

    public enum DIMMER {
        ON,
        OFF,
        INCREASE,
        DECREASE
    }

    public DefaultDimmer(State... states) {
        super(states);
    }

    @Override
    protected String ObjectToString(Object input) {

        if (input == null) {
            return "NULL";
        }

        if (input instanceof DIMMER) {
            return ((DIMMER) input).name();
        }

        if (input instanceof Number) {
            return input.toString();
        }

        if (input instanceof String) {
            return (String) input;
        }

        return "NULL";
    }

    @Override
    public Object convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        DIMMER dimmer = null;

        try {
            dimmer = DIMMER.valueOf(input);
        } catch (IllegalArgumentException e) {}

        if (dimmer != null) {
            return dimmer;
        }

        return Double.valueOf(input);
    }

    @Override
    public String getName() {
        return NAME.DIMMER.name();
    }
}