package com.restaurant.views.order;

import com.restaurant.constants.OrderType;
import com.restaurant.controllers.OrderController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.controllers.RestaurantTableController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;
import com.restaurant.models.Order;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
import com.restaurant.validators.Validator;
import com.restaurant.validators.ValidatorFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class OrderFormDialog extends JDialog {
    private final JComboBox<Restaurant> cbRestaurant = new JComboBox<>();
    private final JComboBox<OrderType> cbType = new JComboBox<>(OrderType.values());
    private final JComboBox<RestaurantTable> cbTable = new JComboBox<>();

    private final OrderController orderController;
    private final RestaurantController restaurantController;
    private final RestaurantTableController tableController;
    private final Runnable onSaved;

    public OrderFormDialog(Frame owner, Runnable onSaved) {
        super(owner, "New Order", true);
        this.orderController = Injector.getInstance().getInstance(OrderController.class);
        this.restaurantController = Injector.getInstance().getInstance(RestaurantController.class);
        this.tableController = Injector.getInstance().getInstance(RestaurantTableController.class);
        this.onSaved = onSaved;
        initRestaurants();
        buildUI();
        cbType.addActionListener(e -> updateFields());
        updateFields();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initRestaurants() {
        List<Restaurant> list = restaurantController.findAllRestaurants();
        for (Restaurant r : list) {
            cbRestaurant.addItem(r);
        }
        cbRestaurant.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof Restaurant r) {
                    setText(r.getName());
                }
                return this;
            }
        });
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Restaurant:"));
        form.add(cbRestaurant);
        form.add(new JLabel("Order Type:"));
        form.add(cbType);
        form.add(new JLabel("Table:"));
        form.add(cbTable);

        JPanel buttons = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        buttons.add(btnSave);
        buttons.add(btnCancel);
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void updateFields() {
        OrderType type = (OrderType) cbType.getSelectedItem();
        boolean dineIn = type == OrderType.DINE_IN;
        cbTable.setEnabled(dineIn);
        if (dineIn) {
            loadTables();
        } else {
            cbTable.removeAllItems();
        }
    }

    private void loadTables() {
        cbTable.removeAllItems();
        Restaurant r = (Restaurant) cbRestaurant.getSelectedItem();
        if (r != null) {
            List<RestaurantTable> tables = tableController.findAllTablesForOrder(r.getId());
            for (RestaurantTable t : tables) {
                cbTable.addItem(t);
            }
        }
    }

    private void onSave() {
        OrderType type = (OrderType) cbType.getSelectedItem();
        int tableId = cbTable.isEnabled() && cbTable.getSelectedItem() != null
                ? ((RestaurantTable) cbTable.getSelectedItem()).getId()
                : 0;

        CreateOrderDto dto = new CreateOrderDto();
        dto.setRestaurantId(
                ((Restaurant) Objects.requireNonNull(cbRestaurant.getSelectedItem())).getId()
        );
        dto.setOrderType(type);
        dto.setRestaurantTableId(type == OrderType.DINE_IN ? tableId : 0);

        Validator<CreateOrderDto, UpdateOrderDto> v =
                ValidatorFactory.getCreateValidator(CreateOrderDto.class);
        if (!v.triggerCreateErrors(dto)) return;

        Order newOrder = orderController.createOrder(dto);
        onSaved.run();
        dispose();
        new OrderUpdateDialog((Frame) getOwner(), newOrder, onSaved).setVisible(true);
    }
}
