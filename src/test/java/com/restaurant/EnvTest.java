package com.restaurant;

import com.restaurant.config.Env;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnvTest {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instance = Env.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void getInstance_returnsSameSingleton() {
        Env e1 = Env.getInstance();
        Env e2 = Env.getInstance();
        assertSame(e1, e2, "getInstance() should always return the same object");
    }

    @Test
    void get_readsFromDotenvWhenNoSystemEnv() throws Exception {
        Env env = Env.getInstance();
        Dotenv mockDotenv = mock(Dotenv.class);
        when(mockDotenv.get("MY_KEY")).thenReturn("fromDotenv");
        Field dotField = Env.class.getDeclaredField("dotenv");
        dotField.setAccessible(true);
        dotField.set(env, mockDotenv);

        String val = env.get("MY_KEY");

        assertEquals("fromDotenv", val);
        verify(mockDotenv).get("MY_KEY");
    }

    @Test
    void get_throwsIfNeitherSystemNorDotenvHaveKey() throws Exception {
        Env env = Env.getInstance();
        Dotenv mockDotenv = mock(Dotenv.class);
        when(mockDotenv.get("ABSENT")).thenReturn(null);
        Field dotField = Env.class.getDeclaredField("dotenv");
        dotField.setAccessible(true);
        dotField.set(env, mockDotenv);

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> env.get("ABSENT"));
        assertEquals("Missing environment variable: ABSENT", ex.getMessage());
    }

    @Test
    void get_throwsIfDotenvReturnsBlank() throws Exception {
        Env env = Env.getInstance();
        Dotenv mockDotenv = mock(Dotenv.class);
        when(mockDotenv.get("BLANK")).thenReturn("   ");
        Field dotField = Env.class.getDeclaredField("dotenv");
        dotField.setAccessible(true);
        dotField.set(env, mockDotenv);

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> env.get("BLANK"));
        assertEquals("Missing environment variable: BLANK", ex.getMessage());
    }

    @Test
    void getWithDefault_returnsDotenvValueIfPresent() throws Exception {
        Env env = Env.getInstance();
        Dotenv mockDotenv = mock(Dotenv.class);
        when(mockDotenv.get("OVERRIDE")).thenReturn("dotenvVal");
        Field dotField = Env.class.getDeclaredField("dotenv");
        dotField.setAccessible(true);
        dotField.set(env, mockDotenv);

        String result = env.get("OVERRIDE", "defaultVal");
        assertEquals("dotenvVal", result);
    }

    @Test
    void getWithDefault_returnsDefaultOnNullOrBlank() throws Exception {
        Env env = Env.getInstance();
        Dotenv mockDotenv = mock(Dotenv.class);
        Field dotField = Env.class.getDeclaredField("dotenv");
        dotField.setAccessible(true);
        dotField.set(env, mockDotenv);

        when(mockDotenv.get("FOO")).thenReturn(null);
        assertEquals("theDefault", env.get("FOO", "theDefault"));

        when(mockDotenv.get("FOO")).thenReturn("  ");
        assertEquals("theDefault", env.get("FOO", "theDefault"));
    }
}