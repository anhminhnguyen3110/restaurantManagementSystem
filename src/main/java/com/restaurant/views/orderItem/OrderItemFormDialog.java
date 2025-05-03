package com.restaurant.views.orderItem;

import com.restaurant.constants.OrderItemStatus;
import com.restaurant.controllers.MenuItemController;
import com.restaurant.controllers.OrderItemController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;
import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.models.OrderItem;
import com.restaurant.validators.Validator;
import com.restaurant.validators.ValidatorFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Objects;

public class OrderItemFormDialog extends JDialog {
    private final JComboBox<MenuItem> cbMenuItem = new JComboBox<>();
    private final JLabel lblPrice = new JLabel();
    private final JTextArea taDescription = new JTextArea(3, 20);
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JTextArea taNotes = new JTextArea(3, 20);
    private final JComboBox<OrderItemStatus> cbStatus = new JComboBox<>(OrderItemStatus.values());
    private final OrderItemController orderItemController;

    public OrderItemFormDialog(Frame owner, Order order, OrderItem existing, Runnable onSaved) {
        super(owner, existing == null ? "Add Item" : "Edit Item", true);
        this.orderItemController = Injector.getInstance().getInstance(OrderItemController.class);
        MenuItemController menuItemController = Injector.getInstance().getInstance(MenuItemController.class);

        for (MenuItem m : menuItemController.findMenuItemsByRestaurantId(order.getRestaurant().getId())) {
            cbMenuItem.addItem(m);
        }
        cbMenuItem.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof MenuItem m) setText(m.getName());
                return this;
            }
        });

        ChangeListener updatePrice = e -> {
            MenuItem m = (MenuItem) cbMenuItem.getSelectedItem();
            int qty = (int) spQty.getValue();
            lblPrice.setText(m != null ? String.format("$%.2f", m.getPrice() * qty) : "");
        };
        cbMenuItem.addActionListener(e -> {
            MenuItem m = (MenuItem) cbMenuItem.getSelectedItem();
            taDescription.setText(m != null ? m.getDescription() : "");
            updatePrice.stateChanged(null);
        });
        spQty.addChangeListener(updatePrice);
        if (existing == null && cbMenuItem.getItemCount() > 0) cbMenuItem.setSelectedIndex(0);

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Menu Item:"));
        form.add(cbMenuItem);
        form.add(new JLabel("Price:"));
        form.add(lblPrice);
        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(taDescription));
        form.add(new JLabel("Quantity:"));
        form.add(spQty);
        form.add(new JLabel("Notes:"));
        form.add(new JScrollPane(taNotes));
        form.add(new JLabel("Status:"));
        form.add(cbStatus);

        JPanel buttons = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        buttons.add(btnSave);
        buttons.add(btnCancel);

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
            if (existing == null) {
                CreateOrderItemDto cd = new CreateOrderItemDto();
                cd.setOrderId(order.getId());
                cd.setMenuItemId(Objects.requireNonNull(m).getId());
                cd.setQuantity(qty);
                cd.setCustomization(notes);

                Validator<CreateOrderItemDto, UpdateOrderItemDto> v =
                        ValidatorFactory.getCreateValidator(CreateOrderItemDto.class);
                if (!v.triggerCreateErrors(cd)) return;

                orderItemController.createOrderItem(cd);
            } else {
                UpdateOrderItemDto ud = new UpdateOrderItemDto();
                ud.setId(existing.getId());
                ud.setOrderId(order.getId());
                ud.setMenuItemId(Objects.requireNonNull(m).getId());
                ud.setQuantity(qty);
                ud.setCustomization(notes);
                ud.setStatus((OrderItemStatus) cbStatus.getSelectedItem());

                Validator<CreateOrderItemDto, UpdateOrderItemDto> v =
                        ValidatorFactory.getUpdateValidator(UpdateOrderItemDto.class);
                if (!v.triggerUpdateErrors(ud)) return;

                orderItemController.updateOrderItem(ud);
            }
            onSaved.run();
            dispose();
        });
        btnCancel.addActionListener(e -> dispose());
    }
}
