package com.restaurant.events;

public class ErrorEvent {
    private final String message;

    public ErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
