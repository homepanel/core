package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public class DefaultState<T> extends Type {

    public enum STATE {
        OPEN,
        CLOSED
    }

    @SuppressWarnings("rawtypes")
    public DefaultState(State... states) {
        super(states);
    }

    @Override
    public String getName() {
        return NAME.STATE.name();
    }
}