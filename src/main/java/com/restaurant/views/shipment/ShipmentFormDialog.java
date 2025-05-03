package com.restaurant.views.shipment;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.controllers.OrderController;
import com.restaurant.controllers.ShipmentController;
import com.restaurant.controllers.UserController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.shipment.CreateShipmentDto;
import com.restaurant.dtos.shipment.UpdateShipmentDto;
import com.restaurant.models.Order;
import com.restaurant.models.Shipment;
import com.restaurant.models.User;
import com.restaurant.validators.Validator;
import com.restaurant.validators.ValidatorFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ShipmentFormDialog extends JDialog {
    private final JComboBox<ShipmentService> cbService = new JComboBox<>(ShipmentService.values());
    private final JComboBox<User> cbShipper = new JComboBox<>();
    private final JComboBox<ShipmentStatus> cbStatus = new JComboBox<>(ShipmentStatus.values());
    private final JTextField txtTracking = new JTextField(15);
    private final JTextField txtName = new JTextField(15);
    private final JTextField txtPhone = new JTextField(15);
    private final JTextField txtEmail = new JTextField(15);
    private final JTextField txtAddress = new JTextField(20);
    private final Order order;
    private final ShipmentController shipmentController;
    private final Runnable onSaved;
    private Shipment shipment;
    private boolean isUpdate;

    public ShipmentFormDialog(Frame owner, Shipment shipment, Runnable onSaved) {
        this(owner, shipment.getOrder().getId(), onSaved);
        isUpdate = true;
        this.shipment = shipment;
        setTitle("Update Shipment #" + shipment.getId());
        cbService.setSelectedItem(shipment.getServiceType());
        cbShipper.setEnabled(shipment.getServiceType() == ShipmentService.INTERNAL);
        cbShipper.setSelectedItem(shipment.getShipper());
        txtName.setText(shipment.getCustomer().getName());
        txtPhone.setText(shipment.getCustomer().getPhoneNumber());
        txtPhone.setEnabled(false);
        txtEmail.setText(shipment.getCustomer().getEmail());
        txtAddress.setText(shipment.getCustomer().getAddress());
        cbStatus.setSelectedItem(shipment.getStatus());
        txtTracking.setText(shipment.getTrackingNumber());
        cbStatus.setEnabled(true);
        txtTracking.setEnabled(false);
    }

    public ShipmentFormDialog(Frame owner, int orderId, Runnable onSaved) {
        super(owner, "Shipment for Order #" + orderId, true);
        this.order = Injector.getInstance().getInstance(OrderController.class).getOrder(orderId);
        this.onSaved = onSaved;
        this.shipmentController = Injector.getInstance().getInstance(ShipmentController.class);
        this.shipment = null;
        isUpdate = false;
        UserController userController = Injector.getInstance().getInstance(UserController.class);
        cbShipper.removeAllItems();
        List<User> shippers = userController.findAllShippers();
        for (User u : shippers) cbShipper.addItem(u);
        cbShipper.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, index, sel, foc);
                if (value instanceof User u) setText(u.getName());
                return this;
            }
        });
        cbShipper.setEnabled(false);
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Service:"));
        form.add(cbService);
        form.add(new JLabel("Shipper:"));
        form.add(cbShipper);
        form.add(new JLabel("Customer Name:"));
        form.add(txtName);
        form.add(new JLabel("Phone:"));
        form.add(txtPhone);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Address:"));
        form.add(txtAddress);
        form.add(new JLabel("Status:"));
        form.add(cbStatus);
        form.add(new JLabel("Tracking #:"));
        form.add(txtTracking);
        cbStatus.setEnabled(false);
        txtTracking.setEnabled(false);
        JPanel buttons = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        buttons.add(btnSave);
        buttons.add(btnCancel);
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        cbService.addActionListener(e -> cbShipper.setEnabled(cbService.getSelectedItem() == ShipmentService.INTERNAL));
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        pack();
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        ShipmentService service = (ShipmentService) cbService.getSelectedItem();
        int shipperId = cbShipper.isEnabled() && cbShipper.getSelectedItem() != null
                ? ((User) cbShipper.getSelectedItem()).getId()
                : 0;
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        if (!isUpdate) {
            CreateShipmentDto dto = new CreateShipmentDto();
            dto.setOrderId(order.getId());
            dto.setServiceType(service);
            dto.setShipperId(shipperId);
            dto.setCustomerName(name);
            dto.setCustomerPhone(phone);
            dto.setCustomerEmail(email);
            dto.setCustomerAddress(address);

            Validator<CreateShipmentDto, UpdateShipmentDto> v =
                    ValidatorFactory.getCreateValidator(CreateShipmentDto.class);
            if (!v.triggerCreateErrors(dto)) return;
            shipmentController.createShipment(dto);
        } else {
            UpdateShipmentDto dto = new UpdateShipmentDto();
            dto.setId(shipment.getId());
            dto.setOrderId(order.getId());
            dto.setServiceType(service);
            dto.setShipperId(shipperId);
            dto.setCustomerName(name);
            dto.setCustomerPhone(phone);
            dto.setCustomerEmail(email);
            dto.setCustomerAddress(address);
            dto.setStatus((ShipmentStatus) cbStatus.getSelectedItem());

            Validator<CreateShipmentDto, UpdateShipmentDto> v =
                    ValidatorFactory.getUpdateValidator(UpdateShipmentDto.class);
            if (!v.triggerUpdateErrors(dto)) return;
            shipmentController.updateShipment(dto);
        }

        onSaved.run();
        dispose();
    }
}
