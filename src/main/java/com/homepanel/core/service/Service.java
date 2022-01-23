package com.homepanel.core.service;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.homepanel.core.config.Broker;
import com.homepanel.core.config.Config;
import com.homepanel.core.config.ConfigTopic;
import com.homepanel.core.config.Job;
import com.homepanel.core.serializer.CustomModule;
import com.homepanel.core.state.Type;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Service<C extends Config> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Service.class);
    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new CustomModule());
    }

    protected final static String MQTT_PATH_SERVICE = "service";
    protected final static String MQTT_PATH_SERVICE_INIT = MQTT_PATH_SERVICE + "/init";
    protected final static String MQTT_PATH_SERVICE_NOTIFICATION = MQTT_PATH_SERVICE + "/notification";
    protected final static String CRON_SCHEDULER_NAME = "cron";

    protected final static String CRON_SCHEDULER_TRIGGER_NAME = "default";
    protected final static String CRON_SCHEDULER_GROUP_NAME = "default";
    protected final static String CRON_SCHEDULER_JOB_NAME = "default";

    public final static String CRON_SCHEDULER_CONTEXT_SERVICE_NAME = "cronService";
    public final static String CRON_SCHEDULER_JOB_DATA_MAP_INTERVAL_NAME = "interval";

    private C config;
    private List<Mqtt3BlockingClient> mqttClients;
    private Integer mqttClientIndex;
    private Timer mqttReconnectTimer;
    private TimerTask mqttReconnectTimerTask;
    private Timer networkHeartbeatTimer;
    private Long mqttReconnectTimeoutInMilliseconds;
    private Long mqttCurrentReconnectTimeoutInMilliseconds;
    private Scheduler cronScheduler;
    private Integer cronSchedulerNumber;
    private Map<String, Map<String, LocalDateTime>> notificationCache;
    private Boolean master;
    private Boolean initOtherServices;
    private Boolean mqtt;

    public C getConfig() {
        return config;
    }

    private void setConfig(C config) {
        this.config = config;
    }

    private List<Mqtt3BlockingClient> getMqttClients() {
        return mqttClients;
    }

    private void setMqttClients(List<Mqtt3BlockingClient> mqttClients) {
        this.mqttClients = mqttClients;
    }

    private Integer getMqttClientIndex() {
        return mqttClientIndex;
    }

    private void setMqttClientIndex(Integer mqttClientIndex) {
        this.mqttClientIndex = mqttClientIndex;
    }

    private Timer getMqttReconnectTimer() {
        return mqttReconnectTimer;
    }

    private void setMqttReconnectTimer(Timer mqttReconnectTimer) {
        this.mqttReconnectTimer = mqttReconnectTimer;
    }

    private TimerTask getMqttReconnectTimerTask() {
        return mqttReconnectTimerTask;
    }

    private void setMqttReconnectTimerTask(TimerTask mqttReconnectTimerTask) {
        this.mqttReconnectTimerTask = mqttReconnectTimerTask;
    }

    private Timer getNetworkHeartbeatTimer() {
        return networkHeartbeatTimer;
    }

    private void setNetworkHeartbeatTimer(Timer networkHeartbeatTimer) {
        this.networkHeartbeatTimer = networkHeartbeatTimer;
    }

    private Long getMqttReconnectTimeoutInMilliseconds() {
        return mqttReconnectTimeoutInMilliseconds;
    }

    private void setMqttReconnectTimeoutInMilliseconds(Long mqttReconnectTimeoutInMilliseconds) {
        this.mqttReconnectTimeoutInMilliseconds = mqttReconnectTimeoutInMilliseconds;
    }

    private Long getMqttCurrentReconnectTimeoutInMilliseconds() {
        return mqttCurrentReconnectTimeoutInMilliseconds;
    }

    private void setMqttCurrentReconnectTimeoutInMilliseconds(Long mqttCurrentReconnectTimeoutInMilliseconds) {
        this.mqttCurrentReconnectTimeoutInMilliseconds = mqttCurrentReconnectTimeoutInMilliseconds;
    }

    protected Scheduler getCronScheduler() {
        return cronScheduler;
    }

    private void setCronScheduler(Scheduler cronScheduler) {
        this.cronScheduler = cronScheduler;
    }

    protected Integer getCronSchedulerNumber() {
        return cronSchedulerNumber;
    }

    private void setCronSchedulerNumber(Integer cronSchedulerNumber) {
        this.cronSchedulerNumber = cronSchedulerNumber;
    }

    protected void increaseCronSchedulerNumber() {
        setCronSchedulerNumber(getCronSchedulerNumber() + 1);
    }

    private Map<String, Map<String, LocalDateTime>> getNotificationCache() {
        return notificationCache;
    }

    private void setNotificationCache(Map<String, Map<String, LocalDateTime>> notificationCache) {
        this.notificationCache = notificationCache;
    }

    protected Boolean isMaster() {
        return master;
    }

    protected void setMaster(Boolean master) {
        this.master = master;
    }

    protected Boolean getMqtt() {
        return mqtt;
    }

    protected void setMqtt(Boolean mqtt) {
        this.mqtt = mqtt;
    }

    public Service() {
        setCronSchedulerNumber(1);
        setNotificationCache(Collections.synchronizedMap(new HashMap<>()));
        this.initOtherServices = false;
    }

    /**
     * start service
     * @param arguments
     * @param configClass
     */
    public void start(String[] arguments, Class<C> configClass) {

        Runtime.getRuntime().addShutdownHook(new Thread() {
                 @Override
                 public void run() {
                     shutdown();
                 }
             }
        );

        setMaster(false);

        // load config from xml
        loadConfig(arguments, configClass);

        setLogging();

        // start mqtt client
        startMqtt();

        try {
            startMqttCronJobs();
        } catch (Exception e) {
            LOGGER.error("global exception when starting service", e);
        }
    }

    /**
     * shutdown
     */
    protected void shutdown() {
        try {
            shutdownService();
        } catch (Exception e) {
            LOGGER.error("could not shutdown service", e);
        }

        try {
            shutdownCronScheduler();
        } catch (Exception e) {
            LOGGER.error("could not shutdown cron scheduler", e);
        }

        if (getMqttReconnectTimerTask() != null) {
            try {
                getMqttReconnectTimerTask().cancel();
            } catch (Exception e) {}
            setMqttReconnectTimerTask(null);
        }

        if (getMqttReconnectTimer() != null) {
            try {
                getMqttReconnectTimer().cancel();
            } catch (Exception e) {}
            getMqttReconnectTimer().purge();
            setMqttReconnectTimer(null);
        }

        if (getNetworkHeartbeatTimer() != null) {
            try {
                getNetworkHeartbeatTimer().cancel();
            } catch (Exception e) {}
            getNetworkHeartbeatTimer().purge();
            setNetworkHeartbeatTimer(null);
        }

        if (getMqttClients() != null && !getMqttClients().isEmpty()) {
            for (Mqtt3BlockingClient mqtt3BlockingClient : getMqttClients()) {
                try {
                    mqtt3BlockingClient.disconnect();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * load config
     * @param configClass
     */
    private void loadConfig(String[] arguments, Class<C> configClass) {

        File configFile = null;
        if (arguments != null && arguments.length > 0 && arguments[0] != null && !arguments[0].isBlank()) {
            configFile = new File(arguments[0]);
        } else {
            configFile = new File("etc/config.xml");
        }

        if (configFile == null) {
            LOGGER.error("no config path defined. exiting", configFile.getAbsolutePath());
            System.exit(1);
        } else if (!configFile.exists()) {
            LOGGER.error("config path \"{}\" not found. exiting", configFile.getAbsolutePath());
            System.exit(1);
        } else if (!configFile.isFile()) {
            LOGGER.error("config path \"{}\" is no file. exiting", configFile.getAbsolutePath());
            System.exit(1);
        }

        if (arguments != null && arguments.length > 1 && arguments[1] != null && !arguments[1].isBlank()) {
            try {
                setMaster(Boolean.valueOf(arguments[1]));
            } catch (Exception e) {}
        }

        JAXBContext jaxbContext;

        try {
            jaxbContext = JAXBContext.newInstance(configClass);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            C object = (C) jaxbUnmarshaller.unmarshal(configFile);

            setConfig(object);

        } catch (JAXBException e) {
            LOGGER.error("can not load config \"" + configFile.getAbsolutePath() + "\". exiting", e);
            System.exit(1);
        }

        if (getConfig().getService().getLogbackXmlPath() != null) {
            if (getConfig().getMqtt() == null) {
                LOGGER.error("no mqtt config found. exiting");
                System.exit(1);
            }
        } else {
            LOGGER.error("no logbackXmlPath found. exiting");
            System.exit(1);
        }
    }

    /**
     * set logback config path
     */
    private void setLogging() {

        File logbackFile = new File(getConfig().getService().getLogbackXmlPath());

        if (!logbackFile.exists()) {
            LOGGER.error("no logback xml path \"{}\" found. exiting", logbackFile.getAbsolutePath());
            System.exit(1);
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(logbackFile);
        } catch (JoranException e) {
            LOGGER.error(String.format("can not load logback config \"%s\". exiting", logbackFile.getAbsolutePath()), e);
            System.exit(1);
        }
    }

    /**
     * subscribe to mqtt
     */
    protected List<String> getMqttTopics() {
        return new ArrayList<>();
    }

    /**
     * start mqtt client
     */
    private void startMqtt() {

        setMqtt(false);

        if (getConfig().getMqtt().getBrokers() != null && !getConfig().getMqtt().getBrokers().isEmpty()) {
            setMqttClients(new ArrayList<>());

            setMqttReconnectTimeoutInMilliseconds(TimeUnit.SECONDS.toMillis(10));
            if (getConfig().getMqtt().getReconnectTimeoutUnit() != null && getConfig().getMqtt().getReconnectTimeoutValue() != null) {
                setMqttReconnectTimeoutInMilliseconds(getConfig().getMqtt().getReconnectTimeoutUnit().toMillis(getConfig().getMqtt().getReconnectTimeoutValue()));
            }
            setMqttCurrentReconnectTimeoutInMilliseconds(getMqttReconnectTimeoutInMilliseconds());
            setMqttReconnectTimer(new Timer());

            if (getMqttClients().isEmpty()) {

                // init connection to brokers
                for (Broker broker : getConfig().getMqtt().getBrokers()) {
                    Mqtt3BlockingClient mqtt3BlockingClient = Mqtt3Client.builder()
                            .identifier(UUID.randomUUID().toString())
                            .serverHost(broker.getHost())
                            .serverPort(broker.getPort())
                            .simpleAuth()
                            .username(broker.getUsername())
                            .password(broker.getPassword().getBytes())
                            .applySimpleAuth()
                            .buildBlocking();

                    getMqttClients().add(mqtt3BlockingClient);
                }

                setMqttClientIndex(0);
            }

            checkReconnectToMqtt();
        }
    }

    private synchronized void checkReconnectToMqtt() {

        reconnectToMqtt();

        if (getMqttClients().get(getMqttClientIndex()).getState() == MqttClientState.CONNECTED) {
            if (!getMqttCurrentReconnectTimeoutInMilliseconds().equals(getMqttReconnectTimeoutInMilliseconds())) {

                LOGGER.warn("reconnected to mqtt broker \"{}:{}\" with username \"{}\"", getConfig().getMqtt().getBrokers().get(getMqttClientIndex()).getHost(), getConfig().getMqtt().getBrokers().get(getMqttClientIndex()).getPort(), getConfig().getMqtt().getBrokers().get(getMqttClientIndex()).getUsername());
                setMqttCurrentReconnectTimeoutInMilliseconds(getMqttReconnectTimeoutInMilliseconds());
            }
        } else {
            setMqtt(false);
            setMqttCurrentReconnectTimeoutInMilliseconds(getMqttCurrentReconnectTimeoutInMilliseconds() + TimeUnit.SECONDS.toMillis(1));
            setMqttCurrentReconnectTimeoutInMilliseconds(Math.min(getMqttCurrentReconnectTimeoutInMilliseconds(), TimeUnit.SECONDS.toMillis(30)));
        }

        if (getMqttReconnectTimer() != null) {

            setMqttReconnectTimerTask(new TimerTask() {
                @Override
                public void run() {
                    setMqttReconnectTimerTask(null);
                    checkReconnectToMqtt();
                }
            });

            getMqttReconnectTimer().schedule(getMqttReconnectTimerTask(), getMqttCurrentReconnectTimeoutInMilliseconds());
        }
    }

    private synchronized void reconnectToMqtt() {

        if (getMqttReconnectTimerTask() != null) {
            try {
                getMqttReconnectTimerTask().cancel();
            } catch (Exception e) {}
            setMqttReconnectTimerTask(null);
        }

        if (getMqttClients().get(getMqttClientIndex()).getState() != MqttClientState.CONNECTED) {

            for (int index = 0; index < getMqttClients().size(); index++) {
                try {
                    getMqttClients().get(index).unsubscribeWith();

                    getMqttClients().get(index).connect();

                    List<String> topics = getMqttTopics();
                    topics.add(MQTT_PATH_SERVICE + "/");

                    for (String topic : topics) {
                        getMqttClients().get(index).toAsync().subscribeWith()
                                .topicFilter(topic + "#")
                                .qos(getConfig().getMqtt().getBrokers().get(index).getQosSubscribe())
                                .callback((mqtt3Publish) -> callbackMqtt(mqtt3Publish))
                                .send();
                    }

                    setMqtt(true);

                    if (this.initOtherServices) {
                        delayedPublishInit();
                        this.initOtherServices = false;
                    }
                } catch (Exception e) {
                    if (getMqttCurrentReconnectTimeoutInMilliseconds().equals(getMqttReconnectTimeoutInMilliseconds())) {
                        LOGGER.error(String.format("can not connect to mqtt broker \"%s:%s\" with username \"%s\"", getConfig().getMqtt().getBrokers().get(index).getHost(), getConfig().getMqtt().getBrokers().get(index).getPort(), getConfig().getMqtt().getBrokers().get(index).getUsername()), e);
                    }
                }

                if (getMqttClients().get(index).getState() == MqttClientState.CONNECTED) {
                    setMqttClientIndex(index);
                    break;
                }
            }
        }
    }

    private void callbackMqtt(Mqtt3Publish mqtt3Publish) {

        String path = mqtt3Publish.getTopic().toString();

        String value = new String(mqtt3Publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if (path.equalsIgnoreCase(MQTT_PATH_SERVICE_INIT)) {
            onInit();
        } else if (path.equalsIgnoreCase(MQTT_PATH_SERVICE_NOTIFICATION)) {

            try {
                Notification notification = OBJECT_MAPPER.readValue(value, Notification.class);

                if (notification != null) {
                    onNotification(notification);
                } else {
                    LOGGER.error("key misssing in json object \"{}\"", value);
                }
            } catch (Exception e) {
                LOGGER.error("can not notify value \"{}\"", value, e);
            }
        } else {
            onData(path, value);
        }
    }

    /**
     * start cron scheduler
     */
    private void startMqttCronJobs() {

        if (getConfig().getMqtt().getJobs() != null && !getConfig().getMqtt().getJobs().isEmpty()) {

            List<String> expressions = new ArrayList<>();

            for (Job job : getConfig().getMqtt().getJobs()) {
                if (job.getExpression() != null && job.getPath() != null) {

                    try {
                        CronScheduleBuilder.cronSchedule(job.getExpression());
                        if (!expressions.contains(job.getExpression())) {
                            expressions.add(job.getExpression());
                        }
                    } catch (Exception e) {
                        LOGGER.error(String.format("no valid cron expression \"%s\"", job.getExpression()), e);
                    }
                }
            }

            if (!expressions.isEmpty()) {

                startCronScheduler();

                for (String expression : expressions) {

                    JobDetail job = JobBuilder.newJob(CronJob.class)
                            .withIdentity(CRON_SCHEDULER_JOB_NAME + getCronSchedulerNumber(), CRON_SCHEDULER_GROUP_NAME)
                            .build();

                    job.getJobDataMap().put(CRON_SCHEDULER_JOB_DATA_MAP_INTERVAL_NAME, expression);

                    CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(expression);

                    Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity(CRON_SCHEDULER_TRIGGER_NAME + getCronSchedulerNumber(), CRON_SCHEDULER_GROUP_NAME)
                            .startNow()
                            .withSchedule(cronScheduleBuilder)
                            .build();

                    increaseCronSchedulerNumber();

                    try {
                        getCronScheduler().scheduleJob(job, trigger);
                    } catch (SchedulerException e) {
                        LOGGER.error(String.format("can not add trigger \"%s\" with group \"%s\" to cron scheduler", trigger.getKey().getName(), trigger.getKey().getGroup()), e);
                    }
                }
            }
        }
    }

    protected void startCronScheduler() {

        if (getCronScheduler() == null) {
            try {
                SimpleThreadPool threadPool = new SimpleThreadPool(1, Thread.MIN_PRIORITY);
                JobStore jobStore = new RAMJobStore();
                threadPool.setInstanceName(CRON_SCHEDULER_NAME);
                DirectSchedulerFactory.getInstance().createScheduler(CRON_SCHEDULER_NAME, UUID.randomUUID().toString(), threadPool, jobStore);
                setCronScheduler(DirectSchedulerFactory.getInstance().getScheduler(CRON_SCHEDULER_NAME));
            } catch (SchedulerException e) {
                LOGGER.error("can not create cron scheduler", e);
                System.exit(1);
            }

            try {
                getCronScheduler().getContext().put(CRON_SCHEDULER_CONTEXT_SERVICE_NAME, this);
            } catch (SchedulerException e) {
                LOGGER.error("can not put object Service to cron scheduler context", e);
            }

            try {
                getCronScheduler().start();
            } catch (SchedulerException e) {
                LOGGER.error("can not start cron scheduler", e);
                System.exit(1);
            }
        }
    }

    /**
     * shutdown cron scheduler
     */
    private void shutdownCronScheduler() {

        if (getCronScheduler() != null) {
            try {
                getCronScheduler().shutdown(true);
                setCronScheduler(null);
            } catch (SchedulerException schedulerException) {
                LOGGER.error("can not shutdown quarz engine scheduler", schedulerException);
            }
        }
    }


    /**
     * start service
     * @throws Exception
     */
    protected abstract void startService() throws Exception;

    /**
     * shutdown service
     * @throws Exception
     */
    protected abstract void shutdownService() throws Exception;

    /**
     * notify to service
     * @param notification
     */
    protected void onNotification(Notification notification) {
    }

    /**
     * initialize data when a request arrives with init
     */
    protected abstract void onInit();

    protected abstract void onData(String path, String value);

    /**
     * publish service init to mqtt broker
     */
    protected void publishInit() {
        if (getMqtt()) {
            delayedPublishInit();
        } else {
            this.initOtherServices = true;
        }
    }

    private void delayedPublishInit() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                publishData(MQTT_PATH_SERVICE_INIT, "true");
            }
        }, 5 * 1000);
    }

    /**
     * publish service notification to mqtt broker
     * @param notification
     */
    public void publishNotification(Notification notification) {
        publishData(MQTT_PATH_SERVICE_NOTIFICATION, ConfigTopic.getType(Type.NAME.JSON).convertObjectToString(notification));
    }
/*
    //publish service notification to mqtt broker
    public void publishNotification(String title, String text, String groupName, Notification.NOTIFICATION_TYPE notificationType, Notification.NOTIFICATION_PRIORITY notificationPriority, String key, TimeUnit timeoutUnit, Long timeoutValue) {

        LocalDateTime now = LocalDateTime.now();

        if (!getNotificationCache().containsKey(groupName)) {
            getNotificationCache().put(groupName, Collections.synchronizedMap(new HashMap<>()));
        }

        if (!getNotificationCache().get(groupName).containsKey(key) || getNotificationCache().get(groupName).get(key).plus(timeoutValue, DateTime.getChronoUnit(timeoutUnit)).isAfter(now)) {
            getNotificationCache().get(groupName).put(key, now);
            publishNotification(title, text, groupName, notificationType, notificationPriority);
        }
    }*/

    protected void publishData(String path, String value) {

        if (getMqttClients() != null && !getMqttClients().isEmpty()) {
            if (getMqttClients().get(getMqttClientIndex()).getState() != MqttClientState.CONNECTED) {
                checkReconnectToMqtt();
            }

            if (getMqttClients().get(getMqttClientIndex()).getState() == MqttClientState.CONNECTED) {
                getMqttClients().get(getMqttClientIndex()).toAsync().publishWith()
                        .topic(path)
                        .payload(value.getBytes())
                        .retain(getConfig().getMqtt().getBrokers().get(getMqttClientIndex()).getRetain())
                        .qos(getConfig().getMqtt().getBrokers().get(getMqttClientIndex()).getQosPublish())
                        .send()
                        .whenComplete((publish, throwable) -> {
                                    if (throwable != null) {
                                        LOGGER.error(String.format("can not publish topic \"%s\" with value \"%s\" to mqtt broker", publish.getTopic().toString(), new String(publish.getPayloadAsBytes())), throwable);
                                    }
                                }
                        );
            } else {
                LOGGER.info("can not write to mqtt topic \"{}\" with value \"{}\" because all brokers are down", path, value);
            }
        } else {
            LOGGER.info("writing to mqtt topic \"{}\" with value \"{}\"", path, value);
        }
    }
}