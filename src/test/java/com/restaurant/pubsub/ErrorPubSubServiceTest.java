package com.restaurant.pubsub;

import com.restaurant.events.ErrorEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ErrorPubSubServiceTest {

    private ErrorPubSubService service;

    @BeforeEach
    void setUp() throws Exception {
        service = ErrorPubSubService.getInstance();
        Field subscribersField = ErrorPubSubService.class.getDeclaredField("subscribers");
        subscribersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Class<?>, List<Consumer<?>>> subscribers =
                (Map<Class<?>, List<Consumer<?>>>) subscribersField.get(service);
        subscribers.clear();
    }

    @Test
    void whenNoSubscribers_publishShouldDoNothing() {
        assertDoesNotThrow(() -> service.publish(new ErrorEvent("no one is listening")));
    }

    @Test
    void singleSubscriber_receivesPublishedEvent() {
        CopyOnWriteArrayList<ErrorEvent> received = new CopyOnWriteArrayList<>();
        service.subscribe(ErrorEvent.class, received::add);
        ErrorEvent evt = new ErrorEvent("oops");
        service.publish(evt);
        assertEquals(1, received.size());
        assertSame(evt, received.get(0));
    }

    @Test
    void multipleSubscribers_allReceiveEvent() {
        CopyOnWriteArrayList<ErrorEvent> rec1 = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<ErrorEvent> rec2 = new CopyOnWriteArrayList<>();
        service.subscribe(ErrorEvent.class, rec1::add);
        service.subscribe(ErrorEvent.class, rec2::add);
        ErrorEvent evt = new ErrorEvent("multi");
        service.publish(evt);
        assertEquals(1, rec1.size());
        assertEquals(1, rec2.size());
        assertSame(evt, rec1.get(0));
        assertSame(evt, rec2.get(0));
    }

    @Test
    void subscribingToOneType_doesNotReceiveOtherType() {
        CopyOnWriteArrayList<Object> received = new CopyOnWriteArrayList<>();
        service.subscribe(Object.class, received::add);
        service.publish(new ErrorEvent("won’t match"));
        assertTrue(received.isEmpty());
    }
}