package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public class DefaultReverseSwitch<T> extends Type {

    public DefaultReverseSwitch(State... states) {
        super(states);
    }

    @Override
    public String getName() {
        return NAME.REVERSE_SWITCH.name();
    }
}