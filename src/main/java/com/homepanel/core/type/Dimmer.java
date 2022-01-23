package com.homepanel.core.type;

import com.homepanel.core.state.State;

public class Dimmer extends DefaultDimmer<Object> {

    public Dimmer() {
        super(
                new State(DIMMER.ON, DIMMER.ON.name()),
                new State(DIMMER.OFF, DIMMER.OFF.name()),
                new State(DIMMER.INCREASE, DIMMER.INCREASE.name()),
                new State(DIMMER.DECREASE, DIMMER.DECREASE.name())
        );
    }
}