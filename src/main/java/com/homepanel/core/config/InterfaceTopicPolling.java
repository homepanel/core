package com.homepanel.core.config;

import java.util.concurrent.TimeUnit;

public interface InterfaceTopicPolling {

    Integer getRefreshIntervalValue();
    void setRefreshIntervalValue(Integer refreshIntervalValue);
    TimeUnit getRefreshIntervalUnit();
    void setRefreshIntervalUnit(TimeUnit refreshIntervalUnit);
}