package com.restaurant.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Injector {
    private static Injector instance;
    private final Map<Class<?>, Object> registry = new HashMap<>();

    private Injector() {
    }

    public static Injector getInstance() {
        if (instance == null) {
            instance = new Injector();
        }
        return instance;
    }

    public <T> void register(Class<T> type, T instance) {
        registry.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type) {
        if (registry.containsKey(type)) {
            return (T) registry.get(type);
        }
        return createInstance(type);
    }

    private <T> T createInstance(Class<T> type) {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            T instance = ctor.newInstance();
            performFieldInjection(instance);
            return instance;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    type.getName() + " must have a no-arg constructor for field injection", e
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + type.getName(), e);
        }
    }

    private void performFieldInjection(Object instance) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Object dependency = getInstance(field.getType());
                field.setAccessible(true);
                field.set(instance, dependency);
            }
        }
    }
}