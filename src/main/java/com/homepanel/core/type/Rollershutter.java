package com.homepanel.core.type;

import com.homepanel.core.state.State;

public class Rollershutter extends DefaultRollershutter<DefaultRollershutter.ROLLERSHUTTER> {

    public Rollershutter() {
        super(
                new com.homepanel.core.state.State(DefaultRollershutter.ROLLERSHUTTER.UP, DefaultRollershutter.ROLLERSHUTTER.UP.name()),
                new com.homepanel.core.state.State(DefaultRollershutter.ROLLERSHUTTER.DOWN, DefaultRollershutter.ROLLERSHUTTER.DOWN.name()),
                new com.homepanel.core.state.State(DefaultRollershutter.ROLLERSHUTTER.STOP, DefaultRollershutter.ROLLERSHUTTER.STOP.name()),
                new State(DefaultRollershutter.ROLLERSHUTTER.MOVE, DefaultRollershutter.ROLLERSHUTTER.MOVE.name())
        );
    }
}