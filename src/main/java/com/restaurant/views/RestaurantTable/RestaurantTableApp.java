package com.restaurant.views.RestaurantTable;

import com.restaurant.views.BaseApp;

import javax.swing.*;

public class RestaurantTableApp extends BaseApp {
    public RestaurantTableApp(BaseApp prevApp) {
        super("Table Map Management", prevApp);
        getContentPane().add(new RestaurantTableMapView());
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            RestaurantTableApp app = new RestaurantTableApp(null);
            app.setVisible(true);
        });
    }
}