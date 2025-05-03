package com.restaurant.validators;

import com.restaurant.events.ErrorEvent;
import com.restaurant.pubsub.ErrorPubSubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private ErrorPubSubService pubSub;

    @BeforeEach
    void clearPubSub() throws Exception {
        pubSub = ErrorPubSubService.getInstance();
        Field subscribersField = ErrorPubSubService.class.getDeclaredField("subscribers");
        subscribersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Class<?>, List<Consumer<?>>> map =
                (Map<Class<?>, List<Consumer<?>>>) subscribersField.get(pubSub);
        map.clear();
    }

    @Test
    void triggerCreateErrors_withErrors_publishesSingleErrorEvent() {
        CopyOnWriteArrayList<ErrorEvent> received = new CopyOnWriteArrayList<>();
        pubSub.subscribe(ErrorEvent.class, received::add);

        Validator<String, String> v = new AlwaysErrorValidator();
        boolean ok = v.triggerCreateErrors("input");
        assertFalse(ok);
        assertEquals(1, received.size());
        assertEquals("Validation errors:\nerr1\nerr2", received.get(0).getMessage());
    }

    @Test
    void triggerCreateErrors_noErrors_returnsTrueAndPublishesNothing() {
        CopyOnWriteArrayList<ErrorEvent> received = new CopyOnWriteArrayList<>();
        pubSub.subscribe(ErrorEvent.class, received::add);

        Validator<String, String> v = new NeverErrorValidator();
        boolean ok = v.triggerCreateErrors("input");
        assertTrue(ok);
        assertTrue(received.isEmpty());
    }

    @Test
    void triggerUpdateErrors_withErrors_publishesSingleErrorEvent() {
        CopyOnWriteArrayList<ErrorEvent> received = new CopyOnWriteArrayList<>();
        pubSub.subscribe(ErrorEvent.class, received::add);

        Validator<String, String> v = new AlwaysErrorValidator();
        boolean ok = v.triggerUpdateErrors("input");
        assertFalse(ok);
        assertEquals(1, received.size());
        assertEquals("Validation errors:\nerr1\nerr2", received.get(0).getMessage());
    }

    @Test
    void triggerUpdateErrors_noErrors_returnsTrueAndPublishesNothing() {
        CopyOnWriteArrayList<ErrorEvent> received = new CopyOnWriteArrayList<>();
        pubSub.subscribe(ErrorEvent.class, received::add);

        Validator<String, String> v = new NeverErrorValidator();
        boolean ok = v.triggerUpdateErrors("input");
        assertTrue(ok);
        assertTrue(received.isEmpty());
    }

    static class AlwaysErrorValidator implements Validator<String, String> {
        @Override
        public List<String> validateCreate(String dto) {
            return Arrays.asList("err1", "err2");
        }

        @Override
        public List<String> validateUpdate(String dto) {
            return Arrays.asList("err1", "err2");
        }
    }

    static class NeverErrorValidator implements Validator<String, String> {
        @Override
        public List<String> validateCreate(String dto) {
            return Collections.emptyList();
        }

        @Override
        public List<String> validateUpdate(String dto) {
            return Collections.emptyList();
        }
    }
}
