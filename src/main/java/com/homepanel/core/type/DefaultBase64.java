package com.homepanel.core.type;

import com.homepanel.core.state.Type;

import java.util.Base64;

public class DefaultBase64 extends Type<byte[]> {

    @Override
    protected String ObjectToString(byte[] input) {

        if (input == null) {
            return "NULL";
        }

        return Base64.getEncoder().encodeToString(input);
    }

    @Override
    public byte[] convertStringToObject(String input) {

        if (input.equals("NULL")) {
            return null;
        }

        return Base64.getDecoder().decode(input);
    }

    @Override
    public String getName() {
        return NAME.BASE64.name();
    }
}