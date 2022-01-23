package com.homepanel.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import java.util.concurrent.TimeUnit;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Mqtt {

    private List<Broker> brokers;
    private Integer reconnectTimeoutValue;
    private TimeUnit reconnectTimeoutUnit;
    private Integer refreshIntervalValue;
    private TimeUnit refreshIntervalUnit;
    private List<Job> jobs;

    @XmlElementWrapper(name = "brokers")
    @XmlElement(name = "broker")
    public List<Broker> getBrokers() {
        return brokers;
    }

    private void setBrokers(List<Broker> brokers) {
        this.brokers = brokers;
    }

    public Integer getReconnectTimeoutValue() {
        return reconnectTimeoutValue;
    }

    private void setReconnectTimeoutValue(Integer reconnectTimeoutValue) {
        this.reconnectTimeoutValue = reconnectTimeoutValue;
    }

    public TimeUnit getReconnectTimeoutUnit() {
        return reconnectTimeoutUnit;
    }

    private void setReconnectTimeoutUnit(TimeUnit reconnectTimeoutUnit) {
        this.reconnectTimeoutUnit = reconnectTimeoutUnit;
    }

    public Integer getRefreshIntervalValue() {
        return refreshIntervalValue;
    }

    private void setRefreshIntervalValue(Integer refreshIntervalValue) {
        this.refreshIntervalValue = refreshIntervalValue;
    }

    public TimeUnit getRefreshIntervalUnit() {
        return refreshIntervalUnit;
    }

    private void setRefreshIntervalUnit(TimeUnit refreshIntervalUnit) {
        this.refreshIntervalUnit = refreshIntervalUnit;
    }

    @XmlElementWrapper(name = "cron")
    @XmlElement(name = "job")
    public List<Job> getJobs() {
        return jobs;
    }

    private void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}