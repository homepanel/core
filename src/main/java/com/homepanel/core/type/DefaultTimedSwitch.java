package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public class DefaultTimedSwitch<T> extends Type {

    @Override
    public Object convertStringToObject(String input) {

        try {
            return Long.valueOf(input);
        } catch (Exception e) {
            return super.convertStringToObject(input);
        }
    }

    public DefaultTimedSwitch(State... states) {
        super(states);
    }

    @Override
    public String getName() {
        return NAME.TIMED_SWITCH.name();
    }
}