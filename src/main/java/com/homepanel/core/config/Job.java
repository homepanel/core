package com.homepanel.core.config;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

public class Job {

    private String expression;
    private String path;

    @XmlAttribute
    public String getExpression() {
        return expression;
    }

    private void setExpression(String expression) {
        this.expression = expression;
    }

    @XmlValue
    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }
}