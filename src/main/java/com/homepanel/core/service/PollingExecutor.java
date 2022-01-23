package com.homepanel.core.service;

import com.homepanel.core.config.InterfaceTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class PollingExecutor<T extends InterfaceTopic> implements Callable<Void> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PollingExecutor.class);

    private PollingService service;
    private T topic;
    private Long jobRunningTimeInMilliseconds;
    private Long refreshIntervalInMilliseconds;

    private PollingService getService() {
        return service;
    }

    private void setService(PollingService service) {
        this.service = service;
    }

    private T getTopic() {
        return topic;
    }

    private void setTopic(T topic) {
        this.topic = topic;
    }

    public Long getJobRunningTimeInMilliseconds() {
        return jobRunningTimeInMilliseconds;
    }

    public void setJobRunningTimeInMilliseconds(Long jobRunningTimeInMilliseconds) {
        this.jobRunningTimeInMilliseconds = jobRunningTimeInMilliseconds;
    }

    public Long getRefreshIntervalInMilliseconds() {
        return refreshIntervalInMilliseconds;
    }

    public void setRefreshIntervalInMilliseconds(Long refreshIntervalInMilliseconds) {
        this.refreshIntervalInMilliseconds = refreshIntervalInMilliseconds;
    }

    public PollingExecutor(PollingService service, T topic, Long jobRunningTimeInMilliseconds, Long refreshIntervalInMilliseconds) {
        setService(service);
        setTopic(topic);
        setJobRunningTimeInMilliseconds(jobRunningTimeInMilliseconds);
        setRefreshIntervalInMilliseconds(refreshIntervalInMilliseconds);
    }

    @Override
    public Void call() throws Exception {

        try {
            getService().pollData(getTopic(), getJobRunningTimeInMilliseconds(), getRefreshIntervalInMilliseconds());
        } catch (Exception e) {
            LOGGER.error("error when executing polling job", e);
        }

        return null;
    }
}