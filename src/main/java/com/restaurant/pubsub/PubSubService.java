package com.restaurant.pubsub;

import java.util.function.Consumer;

public interface PubSubService {
    <T> void subscribe(Class<T> eventType, Consumer<T> consumer);

    void publish(Object event);
}
