package org.example.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class StatsClient {
    protected final RestTemplate rest;

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path);
    }
}
