package com.dmytrobilokha.tyde.infra.websocket;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

public class JsonEncoder implements Encoder.Text<Object> {

    private final Jsonb jsonb = JsonbBuilder.create();
    @Override
    public String encode(Object object) throws EncodeException {
        try {
            return jsonb.toJson(object);
        } catch (JsonbException e) {
            throw new EncodeException(object, "Failed to serialize to JSON object: " + object, e);
        }
    }
}
