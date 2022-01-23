package com.homepanel.core.config;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;


@XmlAccessorType(XmlAccessType.PROPERTY)
public class Broker {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private MqttQos qosPublish;
    private MqttQos qosSubscribe;
    private Boolean retain;

    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    private void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public MqttQos getQosPublish() {
        return qosPublish;
    }

    private void setQosPublish(MqttQos qosPublish) {
        this.qosPublish = qosPublish;
    }

    public MqttQos getQosSubscribe() {
        return qosSubscribe;
    }

    private void setQosSubscribe(MqttQos qosSubscribe) {
        this.qosSubscribe = qosSubscribe;
    }

    public Boolean getRetain() {
        return retain;
    }

    private void setRetain(Boolean retain) {
        this.retain = retain;
    }
}