package com.restaurant.views.orderItem;

import com.restaurant.views.BaseApp;

import javax.swing.*;

public class OrderItemApp extends BaseApp {
    public OrderItemApp(BaseApp prevApp) {
        super("Order Item Management", prevApp);

        getContentPane().add(new OrderItemListView(null, () -> {}));
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            OrderItemApp app = new OrderItemApp(null);
            app.setVisible(true);
        });
    }
}
