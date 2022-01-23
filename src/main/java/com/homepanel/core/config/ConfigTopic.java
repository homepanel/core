package com.homepanel.core.config;

import com.homepanel.core.state.Type;
import com.homepanel.core.type.*;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class ConfigTopic<T extends InterfaceTopic> extends Config {

    private static final Map<String, Type> TYPES = new HashMap<>();

    static {
        addTypes(new DefaultJson(), new DefaultInteger(), new DefaultLong(), new DefaultString(), new DefaultFloat(), new DefaultBoolean(), new Switch(), new Dimmer());
    }

    protected static void addTypes(Type... types) {
        for (Type type : types) {
            TYPES.put(type.getName(), type);
        }
    }

    public static Type getType(Type.NAME name) {
        return TYPES.get(name.name());
    }

    public static Type getType(String name) {
        return TYPES.get(name);
    }

    @XmlTransient
    public abstract List<T> getTopics();

    public abstract void setTopics(List<T> topics);
}