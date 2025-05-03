package com.restaurant.validators;

import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.UpdateBookingDto;
import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;
import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;
import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;
import com.restaurant.dtos.payment.CreatePaymentDto;
import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;
import com.restaurant.dtos.shipment.CreateShipmentDto;
import com.restaurant.dtos.shipment.UpdateShipmentDto;
import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.UpdateUserDto;

import java.util.HashMap;
import java.util.Map;

public class ValidatorFactory {
    private static final Map<Class<?>, Validator<?, ?>> CREATE_REG = new HashMap<>();
    private static final Map<Class<?>, Validator<?, ?>> UPDATE_REG = new HashMap<>();

    static {
        CREATE_REG.put(CreateBookingDto.class, BookingInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateBookingDto.class, BookingInputValidator.INSTANCE);

        CREATE_REG.put(CreateMenuDto.class, MenuInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateMenuDto.class, MenuInputValidator.INSTANCE);

        CREATE_REG.put(CreateMenuItemDto.class, MenuItemInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateMenuItemDto.class, MenuItemInputValidator.INSTANCE);

        CREATE_REG.put(CreateOrderDto.class, OrderInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateOrderDto.class, OrderInputValidator.INSTANCE);

        CREATE_REG.put(CreateOrderItemDto.class, OrderItemInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateOrderItemDto.class, OrderItemInputValidator.INSTANCE);

        CREATE_REG.put(CreatePaymentDto.class, PaymentInputValidator.INSTANCE);

        CREATE_REG.put(CreateShipmentDto.class, ShipmentInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateShipmentDto.class, ShipmentInputValidator.INSTANCE);

        CREATE_REG.put(CreateRestaurantDto.class, RestaurantInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateRestaurantDto.class, RestaurantInputValidator.INSTANCE);

        CREATE_REG.put(CreateRestaurantTableDto.class, RestaurantTableInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateRestaurantTableDto.class, RestaurantTableInputValidator.INSTANCE);

        CREATE_REG.put(CreateUserDto.class, UserInputValidator.INSTANCE);
        UPDATE_REG.put(UpdateUserDto.class, UserInputValidator.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    public static <C, U> Validator<C, U> getCreateValidator(Class<C> cls) {
        return (Validator<C, U>) CREATE_REG.get(cls);
    }

    @SuppressWarnings("unchecked")
    public static <C, U> Validator<C, U> getUpdateValidator(Class<U> cls) {
        return (Validator<C, U>) UPDATE_REG.get(cls);
    }
}
