package com.restaurant.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BaseModelTest {

    @Test
    void testDefaultIdIsZero() {
        DummyModel model = new DummyModel();
        assertEquals(0, model.getId());
    }

    @Test
    void testOnCreateInitializesTimestamps() {
        LocalDateTime before = LocalDateTime.now();
        DummyModel model = new DummyModel();
        model.triggerCreate();
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(model.getCreatedAt());
        assertNotNull(model.getUpdatedAt());
        assertFalse(model.getCreatedAt().isBefore(before));
        assertFalse(model.getCreatedAt().isAfter(after));
        assertFalse(model.getUpdatedAt().isBefore(before));
        assertFalse(model.getUpdatedAt().isAfter(after));
        assertEquals(model.getCreatedAt(), model.getUpdatedAt());
    }

    @Test
    void testOnUpdateUpdatesOnlyUpdatedAt() throws InterruptedException {
        DummyModel model = new DummyModel();
        model.triggerCreate();
        LocalDateTime createdAt = model.getCreatedAt();
        Thread.sleep(10);
        model.triggerUpdate();
        LocalDateTime updatedAt = model.getUpdatedAt();

        assertTrue(updatedAt.isAfter(createdAt));
        assertEquals(createdAt, model.getCreatedAt());
    }

    private static class DummyModel extends BaseModel {
        public void triggerCreate() {
            onCreate();
        }

        public void triggerUpdate() {
            onUpdate();
        }
    }
}