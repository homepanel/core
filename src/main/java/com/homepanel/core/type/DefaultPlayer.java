package com.homepanel.core.type;

import com.homepanel.core.state.State;
import com.homepanel.core.state.Type;

public abstract class DefaultPlayer<T> extends Type {

    public enum PLAYER {
        PLAY,
        PAUSE,
        NEXT,
        PREVIOUS,
        FORWARD,
        FAST_FORWARD,
        REWIND,
        FAST_REWIND
    }

    public DefaultPlayer(State... states) {
        super(states);
    }

    @Override
    public String getName() {
        return NAME.PLAYER.name();
    }
}