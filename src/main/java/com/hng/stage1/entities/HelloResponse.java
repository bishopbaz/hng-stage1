package com.hng.stage1.entities;

public class HelloResponse {
    private final String clientIp;
    private final String location;
    private final String greeting;

    public HelloResponse(String clientIp, String location, String greeting) {
        this.clientIp = clientIp;
        this.location = location;
        this.greeting = greeting;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getLocation() {
        return location;
    }

    public String getGreeting() {
        return greeting;
    }
}
