package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public abstract class DefaultSwitch<T> extends Type {

    public enum SWITCH {
        ON,
        OFF
    }

    public DefaultSwitch(State... states) {
        super(states);
    }

    @Override
    public String getName() {
        return NAME.SWITCH.name();
    }
}