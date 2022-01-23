package com.homepanel.core.config;

import com.homepanel.core.service.Notification;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

public class Group {

    private String name;
    private Notification.NOTIFICATION_PRIORITY threshold;

    @XmlValue
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public Notification.NOTIFICATION_PRIORITY getThreshold() {
        return threshold;
    }

    private void setThreshold(Notification.NOTIFICATION_PRIORITY threshold) {
        this.threshold = threshold;
    }
}