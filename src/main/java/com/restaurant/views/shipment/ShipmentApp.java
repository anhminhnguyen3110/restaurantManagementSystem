package com.restaurant.views.shipment;

import com.restaurant.views.BaseApp;

import javax.swing.*;

public class ShipmentApp extends BaseApp {
    public ShipmentApp(BaseApp prevApp) {
        super("Shipment Management", prevApp);
        getContentPane().add(new ShipmentListView());
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            ShipmentApp app = new ShipmentApp(null);
            app.setVisible(true);
        });
    }
}
