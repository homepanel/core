package com.homepanel.core.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.homepanel.core.state.Type;
import java.util.List;
import java.util.Map;

import static com.homepanel.core.service.Service.OBJECT_MAPPER;

public class DefaultJson extends Type<Object> {

    @Override
    protected String ObjectToString(Object input) {

        if (input == null) {
            return "NULL";
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "NULL";
    }

    @Override
    public Object convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        if (input.trim().startsWith("{")) {
            try {
                return OBJECT_MAPPER.readValue(input, Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if (input.trim().startsWith("[")) {
            try {
                return OBJECT_MAPPER.readValue(input, List.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public String getName() {
        return NAME.JSON.name();
    }
}