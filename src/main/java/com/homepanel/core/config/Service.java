package com.homepanel.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "core-service")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Service {

    private String logbackXmlPath;
    private String i18nPath;

    public String getLogbackXmlPath() {
        return logbackXmlPath;
    }

    private void setLogbackXmlPath(String logbackXmlPath) {
        this.logbackXmlPath = logbackXmlPath;
    }

    public String getI18nPath() {
        return i18nPath;
    }

    private void setI18nPath(String i18nPath) {
        this.i18nPath = i18nPath;
    }
}