package com.restaurant.views.restaurant;

import com.restaurant.views.BaseApp;

import javax.swing.*;

public class RestaurantApp extends BaseApp {
    public RestaurantApp(BaseApp prevApp) {
        super("Restaurant Management", prevApp);
        getContentPane().add(new RestaurantListView());
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            RestaurantApp app = new RestaurantApp(null);
            app.setVisible(true);
        });
    }
}