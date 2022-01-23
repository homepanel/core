package com.homepanel.core.type;

import com.homepanel.core.state.State;

public class ReverseSwitch extends DefaultReverseSwitch<DefaultSwitch.SWITCH> {

    public ReverseSwitch() {
        super(
                new com.homepanel.core.state.State(DefaultSwitch.SWITCH.ON, DefaultSwitch.SWITCH.ON.name()),
                new State(DefaultSwitch.SWITCH.OFF, DefaultSwitch.SWITCH.OFF.name())
        );
    }
}