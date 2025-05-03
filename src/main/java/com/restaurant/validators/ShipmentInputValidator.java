package com.restaurant.validators;

import com.restaurant.constants.ShipmentService;
import com.restaurant.dtos.shipment.CreateShipmentDto;
import com.restaurant.dtos.shipment.UpdateShipmentDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ShipmentInputValidator implements Validator<CreateShipmentDto, UpdateShipmentDto> {
    public static final ShipmentInputValidator INSTANCE = new ShipmentInputValidator();

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\- ]{7,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private ShipmentInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateShipmentDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getServiceType() == null)
            errors.add("• Shipment service is required.");
        if (dto.getOrderId() <= 0)
            errors.add("• Order is required.");
        if (dto.getServiceType() == ShipmentService.INTERNAL && dto.getShipperId() <= 0)
            errors.add("• Shipper must be selected for internal.");
        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank())
            errors.add("• Customer name is required.");
        if (dto.getCustomerPhone() == null || !PHONE_PATTERN.matcher(dto.getCustomerPhone()).matches())
            errors.add("• Valid customer phone is required.");
        if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().isBlank()
                && !EMAIL_PATTERN.matcher(dto.getCustomerEmail()).matches())
            errors.add("• Valid customer email is required.");
        if (dto.getCustomerAddress() == null || dto.getCustomerAddress().isBlank())
            errors.add("• Customer address is required.");
        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateShipmentDto dto) {
        List<String> errors = new ArrayList<>(validateCreate(dto));
        if (dto.getId() <= 0)
            errors.add("• Shipment ID is required.");
        if (dto.getStatus() == null)
            errors.add("• Shipment status is required.");
        return errors;
    }
}
