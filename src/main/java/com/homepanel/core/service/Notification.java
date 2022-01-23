package com.homepanel.core.service;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {

    public enum NOTIFICATION_TYPE {
        INFO,
        SUCCESS,
        WARNING,
        ERROR;
    }

    public enum NOTIFICATION_PRIORITY {
        LOW,
        MEDIUM,
        HIGH,
        URGENT;
    }

    private String id;
    private String title;
    private String text;
    private String groupName;
    private LocalDateTime dateTime;
    private NOTIFICATION_TYPE type;
    private NOTIFICATION_PRIORITY priority;
    private LocalDateTime validUntil;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public NOTIFICATION_TYPE getType() {
        return type;
    }

    public void setType(NOTIFICATION_TYPE type) {
        this.type = type;
    }

    public NOTIFICATION_PRIORITY getPriority() {
        return priority;
    }

    public void setPriority(NOTIFICATION_PRIORITY priority) {
        this.priority = priority;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Notification() {
    }

    public Notification(String title, String text, String groupName, NOTIFICATION_TYPE type, NOTIFICATION_PRIORITY priority) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.text = text;
        this.groupName = groupName;
        this.dateTime = LocalDateTime.now();
        this.type = type;
        this.priority = priority;
    }

    public Notification(String title, String text, String groupName, NOTIFICATION_TYPE type, NOTIFICATION_PRIORITY priority, LocalDateTime validUntil) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.text = text;
        this.groupName = groupName;
        this.dateTime = LocalDateTime.now();
        this.type = type;
        this.priority = priority;
        this.validUntil = validUntil;
    }
}