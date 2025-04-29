package com.restaurant.views.payment;

import com.restaurant.views.BaseApp;

import javax.swing.*;

public class PaymentApp extends BaseApp {
    public PaymentApp(BaseApp prevApp) {
        super("Payment Management", prevApp);
        getContentPane().add(new PaymentListView());
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            PaymentApp app = new PaymentApp(null);
            app.setVisible(true);
        });
    }
}
