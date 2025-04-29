package com.restaurant.views.order;

import com.restaurant.views.BaseApp;
import javax.swing.*;

public class OrderApp extends BaseApp {
    public OrderApp(BaseApp prevApp) {
        super("Order Management", prevApp);

        getContentPane().add(new OrderListView());
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            OrderApp app = new OrderApp(null);
            app.setVisible(true);
        });
    }
}