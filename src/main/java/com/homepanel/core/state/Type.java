package com.homepanel.core.state;

import com.homepanel.core.type.DefaultString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class Type<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Type.class);

    public enum NAME {
        STRING,
        BOOLEAN,
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        JSON,
        DATE,
        DATE_TIME,
        TIME,
        SWITCH,
        TIMED_SWITCH,
        REVERSE_SWITCH,
        STATE,
        CONNECTION,
        ROLLER_SHUTTER,
        PLAYER,
        BASE64,
        DIMMER
    }

    private Map<String, State> states;

    protected Map<String, State> getStates() {
        return states;
    }

    private void setStates(Map<String, State> states) {
        this.states = states;
    }

    public Type(State... states) {

        setStates(new HashMap<>());

        for (State state : states) {
            getStates().put(state.getOutputFormat(), state);
        }
    }

    public State getState(DefaultString name) {
        return getStates().get(name);
    }

    public abstract String getName();

    public String convertObjectToString(Object input) {

        try {
            return ObjectToString((T) input);
        } catch (ClassCastException e) {
            T value = convertStringToObject((String) input);
            return ObjectToString(value);
        }
    }

    protected String ObjectToString(T input) {

        if (input == null) {
            return "NULL";
        }

        for (State state : getStates().values()) {
            if (state.getInputFormat().equals(input)) {
               return state.getOutputFormat();
            }
        }

        LOGGER.info("can not convert object value \"{}\" to string with type \"{}\"", input, getName());

        return null;
    }

    public T convertStringToObject(String input) {
        for (State state : getStates().values()) {
            if (state.getOutputFormat().equals(input)) {
                return (T) state.getInputFormat();
            }
        }

        if (input.equals("NULL")) {
            return null;
        }

        LOGGER.info("can not convert value \"{}\" to object with type \"{}\"", input, getName());

        return null;
    }
}