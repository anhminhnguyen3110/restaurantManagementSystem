package com.restaurant.controllers.impl;

import com.restaurant.constants.ShipmentService;
import com.restaurant.controllers.ShipmentController;
import com.restaurant.daos.CustomerDAO;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.ShipmentDAO;
import com.restaurant.daos.UserDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.shipment.CreateShipmentDto;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.dtos.shipment.UpdateShipmentDto;
import com.restaurant.models.Customer;
import com.restaurant.models.Order;
import com.restaurant.models.Shipment;
import com.restaurant.models.User;

import java.util.List;

@Injectable
public class ShipmentControllerImpl implements ShipmentController {
    @Inject
    private ShipmentDAO shipmentDAO;
    @Inject
    private OrderDAO orderDAO;
    @Inject
    private UserDAO userDAO;
    @Inject
    private CustomerDAO customerDAO;

    public ShipmentControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createShipment(CreateShipmentDto dto) {
        if (shipmentDAO.existsPendingByOrder(dto.getOrderId())) {
            System.out.println("Pending shipment already exists for order " + dto.getOrderId());
            return;
        }
        Order order = orderDAO.getById(dto.getOrderId());
        User shipper = userDAO.getById(dto.getShipperId());
        Customer customer = customerDAO.getByPhoneNumber(dto.getCustomerPhone());
        if (customer == null) {
            customer = new Customer();
            customer.setName(dto.getCustomerName());
            customer.setPhoneNumber(dto.getCustomerPhone());
            customer.setEmail(dto.getCustomerEmail());
            customer.setAddress(dto.getCustomerAddress());
            customerDAO.add(customer);
        }
        Shipment s = new Shipment();
        s.setServiceType(dto.getServiceType());
        s.setOrder(order);
        s.setShipper(shipper);
        s.setCustomer(customer);
        shipmentDAO.add(s);
    }

    @Override
    public void updateShipment(UpdateShipmentDto dto) {
        Shipment s = shipmentDAO.getById(dto.getId());
        if (s == null) return;
        s.setServiceType(dto.getServiceType());
        s.setStatus(dto.getStatus());
        Customer c = s.getCustomer();

        if (dto.getCustomerAddress() != null) {
            c.setAddress(dto.getCustomerAddress());
        }

        if (dto.getCustomerName() != null) {
            c.setName(dto.getCustomerName());
        }

        if (dto.getCustomerEmail() != null) {
            c.setEmail(dto.getCustomerEmail());
        }

        if (dto.getServiceType() == ShipmentService.INTERNAL) {
            User shipper = userDAO.getById(dto.getShipperId());
            s.setShipper(shipper);
        } else {
            s.setShipper(null);
        }

        shipmentDAO.update(s);
    }

    @Override
    public List<Shipment> findShipments(GetShipmentDto dto) {
        return shipmentDAO.find(dto);
    }

    @Override
    public void deleteShipment(int id) {
        shipmentDAO.delete(id);
    }
}