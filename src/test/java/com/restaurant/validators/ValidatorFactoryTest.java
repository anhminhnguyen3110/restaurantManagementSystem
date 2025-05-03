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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ValidatorFactoryTest {

    @Test
    void bookingValidator_registeredForCreateAndUpdate() {
        Validator<CreateBookingDto, UpdateBookingDto> vc =
                ValidatorFactory.getCreateValidator(CreateBookingDto.class);
        Validator<CreateBookingDto, UpdateBookingDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateBookingDto.class);
        assertSame(BookingInputValidator.INSTANCE, vc);
        assertSame(BookingInputValidator.INSTANCE, vu);
    }

    @Test
    void menuValidator_registeredForCreateAndUpdate() {
        Validator<CreateMenuDto, UpdateMenuDto> vc =
                ValidatorFactory.getCreateValidator(CreateMenuDto.class);
        Validator<CreateMenuDto, UpdateMenuDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateMenuDto.class);
        assertSame(MenuInputValidator.INSTANCE, vc);
        assertSame(MenuInputValidator.INSTANCE, vu);
    }

    @Test
    void menuItemValidator_registeredForCreateAndUpdate() {
        Validator<CreateMenuItemDto, UpdateMenuItemDto> vc =
                ValidatorFactory.getCreateValidator(CreateMenuItemDto.class);
        Validator<CreateMenuItemDto, UpdateMenuItemDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateMenuItemDto.class);
        assertSame(MenuItemInputValidator.INSTANCE, vc);
        assertSame(MenuItemInputValidator.INSTANCE, vu);
    }

    @Test
    void orderValidator_registeredForCreateAndUpdate() {
        Validator<CreateOrderDto, UpdateOrderDto> vc =
                ValidatorFactory.getCreateValidator(CreateOrderDto.class);
        Validator<CreateOrderDto, UpdateOrderDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateOrderDto.class);
        assertSame(OrderInputValidator.INSTANCE, vc);
        assertSame(OrderInputValidator.INSTANCE, vu);
    }

    @Test
    void orderItemValidator_registeredForCreateAndUpdate() {
        Validator<CreateOrderItemDto, UpdateOrderItemDto> vc =
                ValidatorFactory.getCreateValidator(CreateOrderItemDto.class);
        Validator<CreateOrderItemDto, UpdateOrderItemDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateOrderItemDto.class);
        assertSame(OrderItemInputValidator.INSTANCE, vc);
        assertSame(OrderItemInputValidator.INSTANCE, vu);
    }

    @Test
    void paymentValidator_registeredForCreateOnly() {
        Validator<CreatePaymentDto, ?> vc =
                ValidatorFactory.getCreateValidator(CreatePaymentDto.class);
        assertSame(PaymentInputValidator.INSTANCE, vc);
    }

    @Test
    void shipmentValidator_registeredForCreateAndUpdate() {
        Validator<CreateShipmentDto, UpdateShipmentDto> vc =
                ValidatorFactory.getCreateValidator(CreateShipmentDto.class);
        Validator<CreateShipmentDto, UpdateShipmentDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateShipmentDto.class);
        assertSame(ShipmentInputValidator.INSTANCE, vc);
        assertSame(ShipmentInputValidator.INSTANCE, vu);
    }

    @Test
    void restaurantValidator_registeredForCreateAndUpdate() {
        Validator<CreateRestaurantDto, UpdateRestaurantDto> vc =
                ValidatorFactory.getCreateValidator(CreateRestaurantDto.class);
        Validator<CreateRestaurantDto, UpdateRestaurantDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateRestaurantDto.class);
        assertSame(RestaurantInputValidator.INSTANCE, vc);
        assertSame(RestaurantInputValidator.INSTANCE, vu);
    }

    @Test
    void restaurantTableValidator_registeredForCreateAndUpdate() {
        Validator<CreateRestaurantTableDto, UpdateRestaurantTableDto> vc =
                ValidatorFactory.getCreateValidator(CreateRestaurantTableDto.class);
        Validator<CreateRestaurantTableDto, UpdateRestaurantTableDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateRestaurantTableDto.class);
        assertSame(RestaurantTableInputValidator.INSTANCE, vc);
        assertSame(RestaurantTableInputValidator.INSTANCE, vu);
    }

    @Test
    void userValidator_registeredForCreateAndUpdate() {
        Validator<CreateUserDto, UpdateUserDto> vc =
                ValidatorFactory.getCreateValidator(CreateUserDto.class);
        Validator<CreateUserDto, UpdateUserDto> vu =
                ValidatorFactory.getUpdateValidator(UpdateUserDto.class);
        assertSame(UserInputValidator.INSTANCE, vc);
        assertSame(UserInputValidator.INSTANCE, vu);
    }

    @Test
    void allCreateValidators_returnNonNull() {
        Class<?>[] createClasses = {
                CreateBookingDto.class,
                CreateMenuDto.class,
                CreateMenuItemDto.class,
                CreateOrderDto.class,
                CreateOrderItemDto.class,
                CreatePaymentDto.class,
                CreateShipmentDto.class,
                CreateRestaurantDto.class,
                CreateRestaurantTableDto.class,
                CreateUserDto.class
        };
        for (Class<?> cls : createClasses) {
            assertNotNull(ValidatorFactory.getCreateValidator(cls),
                    "Missing create‑validator for " + cls.getSimpleName());
        }
    }

    @Test
    void allUpdateValidators_returnNonNullWhereApplicable() {
        Class<?>[] updateClasses = {
                UpdateBookingDto.class,
                UpdateMenuDto.class,
                UpdateMenuItemDto.class,
                UpdateOrderDto.class,
                UpdateOrderItemDto.class,
                UpdateShipmentDto.class,
                UpdateRestaurantDto.class,
                UpdateRestaurantTableDto.class,
                UpdateUserDto.class
        };
        for (Class<?> cls : updateClasses) {
            assertNotNull(ValidatorFactory.getUpdateValidator(cls),
                    "Missing update‑validator for " + cls.getSimpleName());
        }
    }
}
