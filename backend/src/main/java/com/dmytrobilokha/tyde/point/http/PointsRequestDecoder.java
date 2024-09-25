package com.dmytrobilokha.tyde.point.http;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;

import javax.annotation.CheckForNull;

public class PointsRequestDecoder implements Decoder.Text<PointsRequest> {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    @CheckForNull
    public PointsRequest decode(@CheckForNull String s) throws DecodeException {
        if (s == null) {
            return null;
        }
        return jsonb.fromJson(s, PointsRequest.class);
    }

    @Override
    public boolean willDecode(@CheckForNull String s) {
        return true;
    }

}
