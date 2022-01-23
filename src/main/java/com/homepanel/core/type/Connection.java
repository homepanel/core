package com.homepanel.core.type;

import com.homepanel.core.state.State;

public class Connection extends DefaultConnection<DefaultConnection.CONNECTION> {

    public Connection() {
        super(
                new State(DefaultConnection.CONNECTION.ONLINE, DefaultConnection.CONNECTION.ONLINE.name()),
                new State(DefaultConnection.CONNECTION.OFFLINE, DefaultConnection.CONNECTION.OFFLINE.name())
        );
    }
}