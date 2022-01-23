package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public class DefaultConnection<T> extends Type {

    public enum CONNECTION {
        ONLINE,
        OFFLINE
    }

    public DefaultConnection(State... states) {
        super(states);
    }

    @Override
    public String getName() {
        return NAME.CONNECTION.name();
    }
}