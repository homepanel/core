package com.homepanel.core.service;

import com.homepanel.core.config.ConfigTopic;
import com.homepanel.core.config.InterfaceTopic;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class CronJob implements Job {

    private final static Logger LOGGER = LoggerFactory.getLogger(CronJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        SchedulerContext schedulerContext;
        DataService service;

        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e) {
            LOGGER.error("no scheduler context found", e);
            return;
        }

        try {
            service = (DataService) schedulerContext.get(DataService.CRON_SCHEDULER_CONTEXT_SERVICE_NAME);
        } catch (Exception e) {
            LOGGER.error(String.format("can not cast parameter \"%s\" to Service", DataService.CRON_SCHEDULER_CONTEXT_SERVICE_NAME), e);
            return;
        }

        if (context != null && context.getJobDetail() != null && context.getJobDetail().getJobDataMap() != null && context.getJobDetail().getJobDataMap().containsKey(DataService.CRON_SCHEDULER_JOB_DATA_MAP_INTERVAL_NAME)) {
            String expression = context.getJobDetail().getJobDataMap().getString(DataService.CRON_SCHEDULER_JOB_DATA_MAP_INTERVAL_NAME);

            for (com.homepanel.core.config.Job job : service.getConfig().getMqtt().getJobs()) {

                // find a topic with regex in path
                if (job.getExpression().equals(expression)) {

                    String regex = ".*";

                    if (!job.getPath().equals("*")) {

                        regex = job.getPath();
                        if (regex.startsWith("*")) {
                            regex = regex.substring(1);
                        } else {
                            regex = "^" + regex;
                        }
                        if (regex.endsWith("*")) {
                            regex = regex.substring(0, regex.length() - 1);
                        } else {
                            regex = regex + "$";
                        }
                    }

                    Pattern pattern = Pattern.compile(regex);

                    for (Object topic : ((ConfigTopic) service.getConfig()).getTopics()) {

                        if (topic instanceof InterfaceTopic) {

                            InterfaceTopic interfaceTopic = (InterfaceTopic) topic;

                            java.util.regex.Matcher matcher = pattern.matcher(interfaceTopic.getPath());
                            if (matcher.find()) {
                                service.updateData(interfaceTopic);
                            }
                        }
                    }
                }
            }
        } else {
            LOGGER.error("no job data map entry found for key \"{}\"", DataService.CRON_SCHEDULER_JOB_DATA_MAP_INTERVAL_NAME);
        }
    }
}