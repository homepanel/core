package com.homepanel.core.type;

import com.homepanel.core.state.State;

public class Switch extends DefaultSwitch<DefaultSwitch.SWITCH> {

    public Switch() {
        super(
                new com.homepanel.core.state.State(DefaultSwitch.SWITCH.ON, DefaultSwitch.SWITCH.ON.name()),
                new State(DefaultSwitch.SWITCH.OFF, DefaultSwitch.SWITCH.OFF.name())
        );
    }
}