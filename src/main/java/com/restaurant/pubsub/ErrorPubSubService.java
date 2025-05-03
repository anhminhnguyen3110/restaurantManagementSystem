package com.restaurant.pubsub;

import com.restaurant.di.Injectable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Injectable
public class ErrorPubSubService implements PubSubService {
    private static ErrorPubSubService instance;
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    private ErrorPubSubService() {
    }

    public static ErrorPubSubService getInstance() {
        if (instance == null) {
            System.out.println("ErrorPubSubService.getInstance()");
            instance = new ErrorPubSubService();
        }
        return instance;
    }

    @Override
    public <T> void subscribe(Class<T> eventType, Consumer<T> consumer) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    @Override
    public void publish(Object event) {
        List<Consumer<?>> list = subscribers.get(event.getClass());
        if (list != null) {
            for (Consumer subscriber : list) {
                subscriber.accept(event);
            }
        }
    }
}
