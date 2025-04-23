package com.restaurant.order;

import com.restaurant.constants.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTypeTest {

    @Test
    @DisplayName("getType() and toString() return display string")
    void getTypeAndToString() {
        assertEquals("Dine_In",   OrderType.DINE_IN.getType());
        assertEquals("Take_Away", OrderType.TAKE_AWAY.getType());
        assertEquals("Delivery",  OrderType.DELIVERY.getType());

        assertEquals("Dine_In",   OrderType.DINE_IN.toString());
        assertEquals("Take_Away", OrderType.TAKE_AWAY.toString());
        assertEquals("Delivery",  OrderType.DELIVERY.toString());
    }

    @Test
    @DisplayName("fromString() returns correct enum for various cases")
    void fromStringValid() {
        assertSame(OrderType.DINE_IN,   OrderType.fromString("Dine_In"));
        assertSame(OrderType.TAKE_AWAY, OrderType.fromString("Take_Away"));
        assertSame(OrderType.DELIVERY,  OrderType.fromString("Delivery"));

        for (OrderType t : OrderType.values()) {
            assertSame(t, OrderType.fromString(t.getType()));
        }
    }

    @Test
    @DisplayName("fromString() throws on invalid input")
    void fromStringInvalid() {
        String bad = "eat here";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> OrderType.fromString(bad)
        );
        assertEquals("No constant with text " + bad + " found", ex.getMessage());
    }
}