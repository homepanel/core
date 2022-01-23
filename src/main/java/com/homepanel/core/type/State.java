package com.homepanel.core.type;

public class State extends DefaultState<DefaultState.STATE> {

    public State() {
        super(
                new com.homepanel.core.state.State(STATE.OPEN, STATE.OPEN.name()),
                new com.homepanel.core.state.State(STATE.CLOSED, STATE.CLOSED.name())
        );
    }
}
