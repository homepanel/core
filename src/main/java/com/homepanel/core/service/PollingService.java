package com.homepanel.core.service;

import com.homepanel.core.config.ConfigTopic;
import com.homepanel.core.config.InterfaceTopic;
import com.homepanel.core.config.InterfaceTopicPolling;
import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public abstract class PollingService<C extends ConfigTopic, T extends InterfaceTopic> extends DataService<C, T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PollingService.class);

    private final static String POLLING_SCHEDULER_NAME = "polling";

    protected final static String POLLING_SCHEDULER_TRIGGER_NAME = "default";
    protected final static String POLLING_SCHEDULER_GROUP_NAME = "default";
    protected final static String POLLING_SCHEDULER_JOB_NAME = "default";

    public final static String POLLING_SCHEDULER_CONTEXT_SERVICE_NAME = "pollingService";

    private Scheduler pollingScheduler;
    private ExecutorService pollingExecutorService;
    private Map<Long, List<T>> topicsByRefreshIntervalInMilliseconds;

    protected Scheduler getPollingScheduler() {
        return pollingScheduler;
    }

    private void setPollingScheduler(Scheduler pollingScheduler) {
        this.pollingScheduler = pollingScheduler;
    }

    public ExecutorService getPollingExecutorService() {
        return pollingExecutorService;
    }

    private void setPollingExecutorService(ExecutorService pollingExecutorService) {
        this.pollingExecutorService = pollingExecutorService;
    }

    public Map<Long, List<T>> getTopicsByRefreshIntervalInMilliseconds() {
        return topicsByRefreshIntervalInMilliseconds;
    }

    public void setTopicsByRefreshIntervalInMilliseconds(Map<Long, List<T>> topicsByRefreshIntervalInMilliseconds) {
        this.topicsByRefreshIntervalInMilliseconds = topicsByRefreshIntervalInMilliseconds;
    }

    @Override
    public void start(String[] arguments, Class configClass) {

        super.start(arguments, configClass);

        setTopicsByRefreshIntervalInMilliseconds(new HashMap<>());

        try {
             startPollingScheduler();
        } catch (Exception e) {
            LOGGER.error("can not start scheduler", e);
            System.exit(1);
        }

        if (getConfig().getTopics() != null) {
            for (Object topic : getConfig().getTopics()) {

                if (topic instanceof InterfaceTopicPolling) {
                    InterfaceTopicPolling interfaceTopicPolling = (InterfaceTopicPolling) topic;

                    Long refreshIntervalInMilliseconds = interfaceTopicPolling.getRefreshIntervalUnit().toMillis(interfaceTopicPolling.getRefreshIntervalValue());

                    if (!getTopicsByRefreshIntervalInMilliseconds().containsKey(refreshIntervalInMilliseconds)) {
                        getTopicsByRefreshIntervalInMilliseconds().put(refreshIntervalInMilliseconds, new ArrayList<>());
                    }

                    getTopicsByRefreshIntervalInMilliseconds().get(refreshIntervalInMilliseconds).add((T) topic);
                }
            }
        }

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    protected void shutdown() {

        super.shutdown();

        try {
            shutdownPollingScheduler();
        } catch (Exception e) {
            LOGGER.error("global exception when shuting down scheduler", e);
        }
    }

    public List<T> getTopicsByRefreshTime(Long refreshIntervalInMilliseconds) {
        if (getTopicsByRefreshIntervalInMilliseconds().containsKey(refreshIntervalInMilliseconds)) {
            return getTopicsByRefreshIntervalInMilliseconds().get(refreshIntervalInMilliseconds);
        }

        return new ArrayList<>();
    }

    protected List<CronScheduleBuilder> getCronScheduleBuilders() {
        return new ArrayList<>();
    }

    /**
     * start scheduler
     */
    private void startPollingScheduler() {

        /*
            set default refresh value and unit if propertiy in topic not set
            smallest unit is seconds (nano and milliseconds where replace with default value)
         */

        TimeUnit minTimeUnit = TimeUnit.MILLISECONDS;
        Integer minValue = 250;
        TimeUnit defaultTimeUnit = getConfig().getMqtt().getRefreshIntervalUnit();
        Integer defaultValue = getConfig().getMqtt().getRefreshIntervalValue();

        List<Long> refreshIntervalInMilliseconds = new ArrayList<>();

        if (getConfig().getMqtt().getRefreshIntervalValue() != null && getConfig().getMqtt().getRefreshIntervalUnit() != null) {

            Long intervalInMilliseconds = defaultTimeUnit.toMillis(defaultValue);

            if (intervalInMilliseconds.compareTo(minTimeUnit.toMillis(minValue)) < 0) {
                defaultTimeUnit = minTimeUnit;
                defaultValue = minValue;
            }
        }

        for (InterfaceTopic topic : (List<InterfaceTopic>) getConfig().getTopics()) {

            if (topic instanceof InterfaceTopicPolling) {

                InterfaceTopicPolling interfaceTopicPolling = (InterfaceTopicPolling) topic;

                if (interfaceTopicPolling.getRefreshIntervalValue() != null && interfaceTopicPolling.getRefreshIntervalUnit() != null) {

                    Long intervalInMilliseconds = interfaceTopicPolling.getRefreshIntervalUnit().toMillis(interfaceTopicPolling.getRefreshIntervalValue());

                    if (intervalInMilliseconds.compareTo(minTimeUnit.toMillis(minValue)) < 0) {
                        interfaceTopicPolling.setRefreshIntervalUnit(minTimeUnit);
                        interfaceTopicPolling.setRefreshIntervalValue(minValue);
                    }

                } else {
                    interfaceTopicPolling.setRefreshIntervalUnit(defaultTimeUnit);
                    interfaceTopicPolling.setRefreshIntervalValue(defaultValue);
                }

                Long intervalInMilliseconds = interfaceTopicPolling.getRefreshIntervalUnit().toMillis(interfaceTopicPolling.getRefreshIntervalValue());

                if (!refreshIntervalInMilliseconds.contains(intervalInMilliseconds)) {
                    refreshIntervalInMilliseconds.add(intervalInMilliseconds);
                }
            } else {
                System.exit(1);
            }
        }

        try {
            SimpleThreadPool threadPool = new SimpleThreadPool(1, Thread.MIN_PRIORITY);
            JobStore jobStore = new RAMJobStore();
            threadPool.setInstanceName(POLLING_SCHEDULER_NAME);
            DirectSchedulerFactory.getInstance().createScheduler(POLLING_SCHEDULER_NAME, UUID.randomUUID().toString(), threadPool, jobStore);
            setPollingScheduler(DirectSchedulerFactory.getInstance().getScheduler(POLLING_SCHEDULER_NAME));

        } catch (SchedulerException e) {
            LOGGER.error("can not create polling scheduler", e);
            return;
        }

        if (!refreshIntervalInMilliseconds.isEmpty() || !getCronScheduleBuilders().isEmpty()) {
            int number = 1;

            for (long jobRefreshIntervalMilliseconds : refreshIntervalInMilliseconds) {

                JobDetail job = JobBuilder.newJob(PollingJob.class)
                        .withIdentity(POLLING_SCHEDULER_JOB_NAME + number, POLLING_SCHEDULER_GROUP_NAME)
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(POLLING_SCHEDULER_TRIGGER_NAME + number, POLLING_SCHEDULER_GROUP_NAME)
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .withIntervalInMilliseconds((int) jobRefreshIntervalMilliseconds)
                                .repeatForever())
                        .build();

                number++;

                try {
                    getPollingScheduler().scheduleJob(job, trigger);
                } catch (SchedulerException e) {
                    LOGGER.error(String.format("can not add trigger \"%s\" with group \"%s\" to polling scheduler", trigger.getKey().getName(), trigger.getKey().getGroup()), e);
                }
            }

            for (CronScheduleBuilder cronScheduleBuilder : getCronScheduleBuilders()) {

                JobDetail job = JobBuilder.newJob(PollingJob.class)
                        .withIdentity(POLLING_SCHEDULER_JOB_NAME + number, POLLING_SCHEDULER_GROUP_NAME)
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(POLLING_SCHEDULER_TRIGGER_NAME + number, POLLING_SCHEDULER_GROUP_NAME)
                        .startNow()
                        .withSchedule(cronScheduleBuilder)
                        .build();

                number++;

                try {
                    getPollingScheduler().scheduleJob(job, trigger);
                } catch (SchedulerException e) {
                    LOGGER.error(String.format("can not add trigger \"%s\" with group \"%s\" to polling scheduler", trigger.getKey().getName(), trigger.getKey().getGroup()), e);
                }
            }

            try {
                getPollingScheduler().getContext().put(POLLING_SCHEDULER_CONTEXT_SERVICE_NAME, this);
            } catch (SchedulerException e) {
                LOGGER.error("can not put object PollingService to polling scheduler context", e);
            }

            try {
                getPollingScheduler().start();
                setPollingExecutorService(Executors.newFixedThreadPool(getPollingExecutorServicePoolSize()));
            } catch (SchedulerException e) {
                LOGGER.error("can not start polling scheduler", e);
            }
        }
    }

    protected abstract Integer getPollingExecutorServicePoolSize();

    /**
     * shutdown scheduler
     */
    private void shutdownPollingScheduler() {

        if (getPollingScheduler() != null) {
            try {
                getPollingScheduler().shutdown(true);
                setPollingScheduler(null);
            } catch (SchedulerException schedulerException) {
                LOGGER.error("can not shutdown quarz engine scheduler", schedulerException);
            }
        }
    }

    public abstract void pollData(T topic, Long jobRunningTimeInMilliseconds, Long refreshIntervalInMilliseconds);
}
