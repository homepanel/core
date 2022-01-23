package com.homepanel.core.config;

import com.homepanel.core.state.Type;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class TypeAdapter extends XmlAdapter<String, Type> {

    public Type unmarshal(String string) throws Exception {

        Type type = ConfigTopic.getType(string);

        if (type != null) {
            return type;
        }

        throw new Exception(String.format("could not map to \"%s\"", string));
    }

    public String marshal(Type v) throws Exception {
        throw new UnsupportedOperationException();
    }
}