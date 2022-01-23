package com.homepanel.core.service;

import com.homepanel.core.config.ConfigTopic;
import com.homepanel.core.config.InterfaceTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ListenerService<C extends ConfigTopic, T extends InterfaceTopic> extends DataService<C, T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ListenerService.class);

    @Override
    public void start(String[] arguments, Class configClass) {

        super.start(arguments, configClass);

        try {
            startListener();
        } catch (Exception e) {
            LOGGER.error("global exception when starting service", e);
            System.exit(1);
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
            shutdownListener();
        } catch (Exception e) {
            LOGGER.error("global exception when starting service", e);
        }
    }

    /**
     * start listener
     */
    protected abstract void startListener() throws Exception;

    /**
     * shutdown listener
     */
    protected abstract void shutdownListener() throws Exception;
}