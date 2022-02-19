package com.homepanel.core.config;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import java.time.LocalDateTime;
import java.util.List;

@XmlType(name = "core-notification")
public class Notification {

    private com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY threshold;
    private List<Group> groups;

    @XmlAttribute
    public com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY getThreshold() {
        return threshold;
    }

    private void setThreshold(com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY threshold) {
        this.threshold = threshold;
    }

    @XmlElementWrapper(name = "groups")
    @XmlElement(name = "group")
    public List<Group> getGroups() {
        return groups;
    }

    private void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Boolean isNotificationAllowed(com.homepanel.core.service.Notification notification) {

        Boolean result = false;

        if (notification.getValidUntil() == null || notification.getValidUntil().isAfter(LocalDateTime.now())) {
            if (getGroups() != null && !getGroups().isEmpty()) {
                for (Group group : getGroups()) {
                    if (group.getName() != null && group.getName().equals(notification.getGroupName())) {
                        result = isNotificationAllowed(notification.getPriority(), group.getThreshold() != null ? group.getThreshold() : getThreshold());
                        if (result) {
                            break;
                        }
                    }
                }
            } else {
                result = isNotificationAllowed(notification.getPriority(), getThreshold());
            }
        }

        return result;
    }

    private Boolean isNotificationAllowed(com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY notificationPriority, com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY notificationPriorityThreshold) {

        if (notificationPriority != null) {
            if (notificationPriorityThreshold == null) {
                return true;
            } else if (notificationPriorityThreshold == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.LOW) {
                return true;
            } else if (notificationPriorityThreshold == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.MEDIUM && (notificationPriority == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.MEDIUM || notificationPriority == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.HIGH || notificationPriority == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.URGENT)) {
                return true;
            } else if (notificationPriorityThreshold == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.HIGH && (notificationPriority == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.HIGH || notificationPriority == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.URGENT)) {
                return true;
            } else return notificationPriorityThreshold == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.URGENT && notificationPriority == com.homepanel.core.service.Notification.NOTIFICATION_PRIORITY.URGENT;
        }

        return false;
    }
}