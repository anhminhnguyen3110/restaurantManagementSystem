package com.restaurant.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class InjectorTest {

    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = Injector.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private static class NoDefaultConstructor {
        public NoDefaultConstructor(String arg) { }
    }

    private static class ServiceA { }

    private static class ServiceB {
        @Inject
        private ServiceA serviceA;
        public ServiceA getServiceA() {
            return serviceA;
        }
    }

    private static class ServiceC {
        @Inject
        private ServiceB serviceB;
        public ServiceB getServiceB() {
            return serviceB;
        }
    }

    @Test
    void testSingletonInjector() {
        Injector injector1 = Injector.getInstance();
        Injector injector2 = Injector.getInstance();
        assertSame(injector1, injector2);
    }

    @Test
    void testRegisterAndRetrieve() {
        Injector injector = Injector.getInstance();
        ServiceA instance = new ServiceA();
        injector.register(ServiceA.class, instance);
        ServiceA result = injector.getInstance(ServiceA.class);
        assertSame(instance, result);
    }

    @Test
    void testNewInstanceForUnregisteredType() {
        Injector injector = Injector.getInstance();
        ServiceA a1 = injector.getInstance(ServiceA.class);
        ServiceA a2 = injector.getInstance(ServiceA.class);
        assertNotSame(a1, a2);
    }

    @Test
    void testCreateInstanceAndFieldInjection() {
        Injector injector = Injector.getInstance();
        ServiceB result = injector.getInstance(ServiceB.class);
        assertNotNull(result);
        assertNotNull(result.getServiceA());
    }

    @Test
    void testNestedFieldInjection() {
        Injector injector = Injector.getInstance();
        ServiceC result = injector.getInstance(ServiceC.class);
        assertNotNull(result);
        assertNotNull(result.getServiceB());
        assertNotNull(result.getServiceB().getServiceA());
    }

    @Test
    void testMissingNoArgConstructorThrows() {
        Injector injector = Injector.getInstance();
        assertThrows(RuntimeException.class, () -> injector.getInstance(NoDefaultConstructor.class));
    }

    @Test
    void testRegisterOverridesDefault() {
        Injector injector = Injector.getInstance();
        ServiceA first = new ServiceA();
        injector.register(ServiceA.class, first);
        ServiceA second = new ServiceA();
        injector.register(ServiceA.class, second);
        ServiceA result = injector.getInstance(ServiceA.class);
        assertSame(second, result);
    }
}
