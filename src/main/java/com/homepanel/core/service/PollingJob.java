package com.homepanel.core.service;

import com.homepanel.core.config.InterfaceTopic;
import com.homepanel.core.config.InterfaceTopicPolling;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class PollingJob<T extends InterfaceTopic> implements Job {

    private final static Logger LOGGER = LoggerFactory.getLogger(PollingJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        SchedulerContext schedulerContext;
        PollingService pollingService;

        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e) {
            LOGGER.error("no scheduler context found", e);
            return;
        }

        try {
            pollingService = (PollingService) schedulerContext.get(PollingService.POLLING_SCHEDULER_CONTEXT_SERVICE_NAME);
        } catch (Exception e) {
            LOGGER.error(String.format("can not cast parameter \"%s\" to PollingService", PollingService.POLLING_SCHEDULER_CONTEXT_SERVICE_NAME), e);
            return;
        }

        if (pollingService == null) {
            LOGGER.error("polling service is null");
            return;
        }

        Date nextFireTime = context.getTrigger().getNextFireTime();

        if (nextFireTime == null) {
            LOGGER.error("next fire time is null");
            return;
        }

        Date subsequentFireTime = context.getTrigger().getFireTimeAfter(nextFireTime);
        if (subsequentFireTime != null) {
            Long refreshIntervalInMilliseconds = subsequentFireTime.getTime() - nextFireTime.getTime();

            if (refreshIntervalInMilliseconds != null) {
                for (Object topic : pollingService.getTopicsByRefreshTime(refreshIntervalInMilliseconds)) {

                    if (topic instanceof InterfaceTopicPolling) {

                        InterfaceTopicPolling interfaceTopicPolling = (InterfaceTopicPolling) topic;

                        PollingExecutor pollingExecutor = new PollingExecutor<T>(pollingService, (T) topic, context.getScheduledFireTime().getTime(), refreshIntervalInMilliseconds);

                        pollingService.getPollingExecutorService().submit(pollingExecutor);
                    }
                }
            } else {
                LOGGER.error("refreshIntervalInMilliseconds is null");
            }
        } else {
            LOGGER.error("fire after time is null");
        }
    }
}