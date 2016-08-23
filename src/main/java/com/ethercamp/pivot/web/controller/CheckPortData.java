package com.ethercamp.pivot.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

/**
 * Created by Stan Reshetnyk on 22.08.16.
 */
@ToString
public class CheckPortData {

    public final int port;

    public final String protocol;

    @JsonCreator
    public CheckPortData(
            @JsonProperty("port")       int port,
            @JsonProperty("protocol")   String protocol) {
        this.port = port;
        this.protocol = protocol;
    }
}
