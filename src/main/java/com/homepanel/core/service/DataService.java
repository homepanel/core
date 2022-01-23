package com.homepanel.core.service;

import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.core.config.ConfigTopic;
import com.homepanel.core.config.InterfaceTopic;
import com.homepanel.core.config.InterfaceTopicValue;
import com.homepanel.core.type.DefaultSwitch;
import com.homepanel.core.type.DefaultTimedSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataService<C extends ConfigTopic, T extends InterfaceTopic> extends Service<C> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    private Map<String, T> topicsByPath;
    private Map<String, T> topicsByParameter;
    private List<String> executingPaths;

    private Map<String, T> getTopicsByPath() {
        return topicsByPath;
    }

    private void setTopicsByPath(Map<String, T> topicsByPath) {
        this.topicsByPath = topicsByPath;
    }

    private Map<String, T> getTopicsByParameter() {
        return topicsByParameter;
    }

    private void setTopicsByParameter(Map<String, T> topicsByParameter) {
        this.topicsByParameter = topicsByParameter;
    }

    private List<String> getExecutingPaths() {
        return executingPaths;
    }

    private void setExecutingPaths(List<String> executingPaths) {
        this.executingPaths = executingPaths;
    }

    public void start(String[] arguments, Class configClass) {

        setExecutingPaths(new ArrayList<>());

        super.start(arguments, configClass);

        // load topics
        loadTopics();

        try {
            startService();
        } catch (Exception e) {
            LOGGER.error("global exception when starting service", e);
        }
    }

    /**
     * load topics
     */
    private void loadTopics() {

        setTopicsByPath(new HashMap<>());
        setTopicsByParameter(new HashMap<>());

        if (getConfig().getTopics() != null) {
            for (Object topic : getConfig().getTopics()) {
                if (topic instanceof InterfaceTopic) {
                    T interfaceTopic = (T) topic;
                    getTopicsByPath().put(interfaceTopic.getPath(), interfaceTopic);
                    getTopicsByParameter().put(getTopicNameByTopic(interfaceTopic), interfaceTopic);
                }
            }
        }
    }

    public T getTopicByPath(String path) {

        if (getTopicsByPath().containsKey(path)) {
            return getTopicsByPath().get(path);
        }

        return null;
    }

    public T getTopicByParameter(Object... parameter) {

        String name = getTopicNameByParameter(parameter);

        if (getTopicsByParameter().containsKey(name)) {
            return getTopicsByParameter().get(name);
        }

        return null;
    }

    public String getTopicNameByParameter(Object... parameter) {

        StringBuilder sb = new StringBuilder();
        for (Object object : parameter) {

            if (sb.length() > 0) {
                sb.append("-");
            }

            sb.append(object != null ? object : "NULL");
        }

        return sb.toString();
    }

    protected List<String> getMqttTopics() {

        List<String> topics = new ArrayList<>();

        for (InterfaceTopic topic : (List<InterfaceTopic>) getConfig().getTopics()) {
            if (topic.getPath() != null) {
                int pos = topic.getPath().indexOf("/");
                if (pos != -1) {
                    String path = topic.getPath().substring(0, pos + 1);
                    if (!topics.contains(path)) {
                        topics.add(path);
                    }
                }
            }
        }

        return topics;
    }

    public abstract String getTopicNameByTopic(T topic);

    /**
     * read data from mqtt
     * @param path
     * @param value
     */
    @Override
    protected void onData(String path, String value) {

        if (path.toLowerCase().endsWith("/write")) {
            path = path.substring(0, path.length() - 6);

            LOGGER.info("reading topic \"{}\" with value \"{}\"", path, value);

            T topic = getTopicByPath(path);

            if (topic != null) {
                if (topic.getType() != null) {
                    Object val = topic.getType().convertStringToObject(value);

                    if (!getExecutingPaths().contains(topic.getPath())) {
                        if (topic.getType() instanceof DefaultTimedSwitch && val != null && val instanceof Long) {

                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        getExecutingPaths().add(topic.getPath());

                                        onData(topic, topic.getType().convertStringToObject(DefaultSwitch.SWITCH.ON.name()), PriorityThreadPoolExecutor.PRIORITY.HIGHEST);
                                        publishData(topic.getPath(), DefaultSwitch.SWITCH.ON.name());

                                        sleep((Long) val);

                                        onData(topic, topic.getType().convertStringToObject(DefaultSwitch.SWITCH.OFF.name()), PriorityThreadPoolExecutor.PRIORITY.HIGHEST);
                                        publishData(topic.getPath(), DefaultSwitch.SWITCH.OFF.name());

                                        getExecutingPaths().remove(topic.getPath());

                                    } catch (Exception e) {
                                        getExecutingPaths().remove(topic.getPath());
                                    }
                                }
                            }.start();
                        } else {
                            if (topic instanceof InterfaceTopicValue) {
                                InterfaceTopicValue interfaceTopicValue = (InterfaceTopicValue) topic;

                                interfaceTopicValue.setLastValue(value);
                                interfaceTopicValue.setLastDateTime(LocalDateTime.now());
                            }

                            onData(topic, val, PriorityThreadPoolExecutor.PRIORITY.HIGHEST);
                        }

                        LOGGER.info("reading topic with path \"{}\" with value \"{}\"", topic.getPath(), val);
                    }
                } else {
                    LOGGER.error("can not convert because type is null");
                }
            }
        }
    }

    /**
     * reading data from mqtt topic
     * triggert by mqtt subscriber for all data with topic and set method
     * @param value
     * @param topic
     * @param priority
     */
    protected abstract void onData(T topic, Object value, PriorityThreadPoolExecutor.PRIORITY priority);

    /**
     * writing data to mqtt topic
     * with topic and get method
     * @param value
     * @param topic
     */
    public void publishData(T topic, Object value) {

        if (topic != null) {

            if (topic instanceof InterfaceTopicValue) {
                InterfaceTopicValue interfaceTopicValue = (InterfaceTopicValue) topic;
                if (interfaceTopicValue.getLastValue() != null && interfaceTopicValue.getLastDateTime() != null && interfaceTopicValue.getLastDateTime().plusSeconds(60).isAfter(LocalDateTime.now()) && !interfaceTopicValue.getLastValue().equals(value)) {
                    // skip because data is not valid
                    return;
                }
            }

            if (topic.getType() != null) {
                String result = topic.getType().convertObjectToString(value);

                String path = topic.getPath() + "/read";

                while (path.startsWith("/")) {
                    path = path.substring(1);
                }

                while (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }

                publishData(path, result);
            } else {
                LOGGER.error("can not convert because type is null");
            }
        } else {
            LOGGER.debug("can not find mapping for path \"{}\"", topic.getPath());
        }
    }

    /**
     * update data
     * cron scheduler triggers update data method for updating data import
     * @param topic
     */
    protected abstract void updateData(T topic);
}
