package com.restaurant.views.order;

import com.restaurant.controllers.MenuItemController;
import com.restaurant.controllers.OrderController;
import com.restaurant.controllers.OrderItemController;
import com.restaurant.controllers.ShipmentController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.models.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AddOrderItemDialog extends JDialog {
    private final JComboBox<MenuItem> cbItem = new JComboBox<>();
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JTextField txtCust = new JTextField(15);
    private final OrderItemController itemCtrl;
    private final ShipmentController shipmentCtrl;
    private final OrderController orderController;
    private final Order order;

    public AddOrderItemDialog(Dialog owner, Order order, Consumer<Void> onAdded) {
        super(owner, "Add Item to Order", true);
        this.order = order;
        itemCtrl = Injector.getInstance().getInstance(OrderItemController.class);
        shipmentCtrl = Injector.getInstance().getInstance(ShipmentController.class);
        orderController = Injector.getInstance().getInstance(OrderController.class);
        List<MenuItem> items = Injector.getInstance()
                .getInstance(MenuItemController.class)
                .findMenuItems(new com.restaurant.dtos.menuItem.GetMenuItemsDto() {{
                    setPage(0);
                    setSize(100);
                }});
        items.forEach(cbItem::addItem);
        JPanel f = new JPanel(new GridLayout(3, 2, 5, 5));
        f.add(new JLabel("Item:"));
        f.add(cbItem);
        f.add(new JLabel("Quantity:"));
        f.add(spQty);
        f.add(new JLabel("Customization:"));
        f.add(txtCust);
        JPanel b = new JPanel();
        JButton btnAdd = new JButton("Add");
        b.add(btnAdd);
        JButton btnCancel = new JButton("Cancel");
        b.add(btnCancel);
        btnAdd.addActionListener(e -> {
            CreateOrderItemDto dto = new CreateOrderItemDto();
            dto.setOrderId(order.getId());
            dto.setMenuItemId(((MenuItem) Objects.requireNonNull(cbItem.getSelectedItem())).getId());
            dto.setQuantity((int) spQty.getValue());
            dto.setCustomization(txtCust.getText().trim());
            itemCtrl.createOrderItem(dto);
            onAdded.accept(null);
            dispose();
        });
        btnCancel.addActionListener(e -> onCancel());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        getContentPane().add(f, BorderLayout.CENTER);
        getContentPane().add(b, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    private void onCancel() {
        GetOrderItemDto checkDto = new GetOrderItemDto();
        checkDto.setOrderId(order.getId());
        List<OrderItem> items = itemCtrl.findOrderItems(checkDto);
        GetShipmentDto shipDto = new GetShipmentDto();
        shipDto.setOrderId(order.getId());
        boolean hasShipment = !shipmentCtrl.findShipments(shipDto).isEmpty();
        if (items.isEmpty() && !hasShipment) {
            orderController.deleteOrder(order.getId());
        }
        dispose();
    }
}
