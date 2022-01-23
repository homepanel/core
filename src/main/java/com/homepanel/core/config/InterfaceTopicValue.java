package com.homepanel.core.config;

import java.time.LocalDateTime;

public interface InterfaceTopicValue {

    Object getLastValue();
    void setLastValue(Object lastValue);
    LocalDateTime getLastDateTime();
    void setLastDateTime(LocalDateTime lastDateTime);
}