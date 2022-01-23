package com.homepanel.core.type;

import com.homepanel.core.state.State;

public class Player extends DefaultPlayer<DefaultPlayer.PLAYER> {

    public Player() {
        super(
                new com.homepanel.core.state.State(PLAYER.PLAY, PLAYER.PLAY.name()),
                new com.homepanel.core.state.State(PLAYER.PAUSE, PLAYER.PAUSE.name()),
                new com.homepanel.core.state.State(PLAYER.NEXT, PLAYER.NEXT.name()),
                new com.homepanel.core.state.State(PLAYER.PREVIOUS, PLAYER.PREVIOUS.name()),
                new com.homepanel.core.state.State(PLAYER.FORWARD, PLAYER.FORWARD.name()),
                new com.homepanel.core.state.State(PLAYER.FAST_FORWARD, PLAYER.FAST_FORWARD.name()),
                new com.homepanel.core.state.State(PLAYER.REWIND, PLAYER.REWIND.name()),
                new State(PLAYER.FAST_REWIND, PLAYER.FAST_REWIND.name())
        );
    }
}