package com.homepanel.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "core-config")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Config {

    private Service service;
    private Mqtt mqtt;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Mqtt getMqtt() {
        return mqtt;
    }

    private void setMqtt(Mqtt mqtt) {
        this.mqtt = mqtt;
    }
}