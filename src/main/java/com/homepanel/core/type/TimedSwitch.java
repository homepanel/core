package com.homepanel.core.type;

import com.homepanel.core.state.State;

public class TimedSwitch extends DefaultTimedSwitch<DefaultSwitch.SWITCH> {

    public TimedSwitch() {
        super(
                new State(DefaultSwitch.SWITCH.ON, DefaultSwitch.SWITCH.ON.name()),
                new State(DefaultSwitch.SWITCH.OFF, DefaultSwitch.SWITCH.OFF.name())
        );
    }
}