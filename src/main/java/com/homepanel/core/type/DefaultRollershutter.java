package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public class DefaultRollershutter<T> extends Type {

    public enum ROLLERSHUTTER {
        UP,
        DOWN,
        STOP,
        MOVE
    }

    public DefaultRollershutter(State... states) {
        super(states);
    }

    @Override
    public String getName() {
        return NAME.ROLLER_SHUTTER.name();
    }
}