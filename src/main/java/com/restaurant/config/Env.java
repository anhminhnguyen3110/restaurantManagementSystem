package com.restaurant.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class Env {

    private static volatile Env INSTANCE;
    private final Dotenv dotenv;

    private Env() {
        this.dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }

    public static Env getInstance() {
        if (INSTANCE == null) {
            synchronized (Env.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Env();
                }
            }
        }
        return INSTANCE;
    }

    public String get(String key) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            val = dotenv.get(key);
        }
        if (val == null || val.isBlank()) {
            throw new IllegalStateException("Missing environment variable: " + key);
        }
        return val;
    }

    public String get(String key, String defaultVal) {
        String val = System.getenv().getOrDefault(key, dotenv.get(key));
        return (val == null || val.isBlank()) ? defaultVal : val;
    }
}