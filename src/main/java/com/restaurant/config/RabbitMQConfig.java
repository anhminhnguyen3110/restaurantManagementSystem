package com.restaurant.config;

import com.rabbitmq.client.ConnectionFactory;

public final class RabbitMQConfig {

    private static volatile RabbitMQConfig INSTANCE;
    private final ConnectionFactory factory;

    private RabbitMQConfig() {
        this.factory = initFactory();
    }

    public static RabbitMQConfig getInstance() {
        if (INSTANCE == null) {
            synchronized (RabbitMQConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RabbitMQConfig();
                }
            }
        }
        return INSTANCE;
    }

    private ConnectionFactory initFactory() {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(Env.getInstance().get("MQ_HOST", "localhost"));
        cf.setPort(Integer.parseInt(Env.getInstance().get("MQ_PORT", "5672")));
        cf.setUsername(Env.getInstance().get("MQ_USER", "guest"));
        cf.setPassword(Env.getInstance().get("MQ_PASS", "guest"));
        cf.setVirtualHost(Env.getInstance().get("MQ_VHOST", "/"));
        cf.setAutomaticRecoveryEnabled(true);
        return cf;
    }

    public ConnectionFactory getFactory() {
        return factory;
    }
}