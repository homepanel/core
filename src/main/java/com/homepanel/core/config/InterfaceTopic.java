package com.homepanel.core.config;

import com.homepanel.core.state.Type;

public interface InterfaceTopic {

    String getPath();
    void setPath(String path);
    Type getType();
    void setType(Type type);
}