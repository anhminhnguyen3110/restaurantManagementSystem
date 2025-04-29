package com.restaurant.views.orderItem;

import com.restaurant.controllers.MenuItemController;
import com.restaurant.controllers.OrderItemController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;
import com.restaurant.models.Order;
import com.restaurant.models.OrderItem;
import com.restaurant.models.MenuItem;
import com.restaurant.constants.OrderItemStatus;
import com.restaurant.utils.validators.OrderItemInputValidator;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.List;

public class OrderItemFormDialog extends JDialog {
    private final JComboBox<MenuItem> cbMenuItem = new JComboBox<>();
    private final JLabel lblPrice = new JLabel();
    private final JTextArea taDescription = new JTextArea(3, 20);
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JTextArea taNotes = new JTextArea(3, 20);
    private final JComboBox<OrderItemStatus> cbStatus = new JComboBox<>(OrderItemStatus.values());
    private final JButton btnSave = new JButton("Save"), btnCancel = new JButton("Cancel");

    private final OrderItemController orderItemController;
    private final MenuItemController menuItemController;

    public OrderItemFormDialog(Frame owner, Order order, OrderItem existing, Runnable onSaved) {
        super(owner, existing == null ? "Add Item" : "Edit Item", true);
        this.orderItemController = Injector.getInstance().getInstance(OrderItemController.class);
        this.menuItemController = Injector.getInstance().getInstance(MenuItemController.class);

        List<MenuItem> list = menuItemController.findMenuItemsByRestaurantId(order.getRestaurant().getId());
        for (MenuItem m : list) cbMenuItem.addItem(m);

        cbMenuItem.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f){
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof MenuItem m) setText(m.getName());
                return this;
            }
        });

        ChangeListener updatePrice = e -> {
            MenuItem m = (MenuItem) cbMenuItem.getSelectedItem();
            int qty = (int) spQty.getValue();
            if (m != null) {
                lblPrice.setText(String.format("$%.2f", m.getPrice() * qty));
            } else {
                lblPrice.setText("");
            }
        };

        cbMenuItem.addActionListener(e -> {
            MenuItem m = (MenuItem) cbMenuItem.getSelectedItem();
            taDescription.setText(m != null ? m.getDescription() : "");
            updatePrice.stateChanged(null);
        });

        spQty.addChangeListener(updatePrice);

        if (existing == null && cbMenuItem.getItemCount() > 0) {
            cbMenuItem.setSelectedIndex(0);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Menu Item:"));    form.add(cbMenuItem);
        form.add(new JLabel("Price:"));        form.add(lblPrice);
        form.add(new JLabel("Description:"));  form.add(new JScrollPane(taDescription));
        form.add(new JLabel("Quantity:"));     form.add(spQty);
        form.add(new JLabel("Notes:"));        form.add(new JScrollPane(taNotes));
        form.add(new JLabel("Status:"));       form.add(cbStatus);

        JPanel buttons = new JPanel();
        buttons.add(btnSave); buttons.add(btnCancel);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);

        if (existing != null) {
            cbMenuItem.setSelectedItem(existing.getMenuItem());
            spQty.setValue(existing.getQuantity());
            taNotes.setText(existing.getCustomization());
            cbStatus.setSelectedItem(existing.getStatus());
        } else {
            cbStatus.setSelectedIndex(0);
            cbStatus.setEnabled(false);
        }

        updatePrice.stateChanged(null);

        btnSave.addActionListener(e -> {
            MenuItem m = (MenuItem) cbMenuItem.getSelectedItem();
            int qty = (int) spQty.getValue();
            String notes = taNotes.getText().trim();
            OrderItemStatus status = (OrderItemStatus) cbStatus.getSelectedItem();
            var errors = OrderItemInputValidator.validate(m, qty);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (existing == null) {
                CreateOrderItemDto cd = new CreateOrderItemDto();
                cd.setOrderId(order.getId());
                cd.setMenuItemId(m.getId());
                cd.setQuantity(qty);
                cd.setCustomization(notes);
                orderItemController.createOrderItem(cd);
            } else {
                UpdateOrderItemDto ud = new UpdateOrderItemDto();
                ud.setId(existing.getId());
                ud.setMenuItemId(m.getId());
                ud.setQuantity(qty);
                ud.setCustomization(notes);
                ud.setStatus(status);
                orderItemController.updateOrderItem(ud);
            }
            onSaved.run();
            dispose();
        });

        btnCancel.addActionListener(e -> dispose());
    }
}
