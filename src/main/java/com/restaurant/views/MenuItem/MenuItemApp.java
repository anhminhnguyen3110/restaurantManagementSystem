package com.restaurant.views.MenuItem;

import com.restaurant.views.BaseApp;

import javax.swing.*;

public class MenuItemApp extends BaseApp {
    public MenuItemApp(BaseApp prevApp) {
        super("Menu Item Management", prevApp);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Menu Items", new MenuItemListView());
        getContentPane().add(tabs);
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            MenuItemApp app = new MenuItemApp(null);
            app.setVisible(true);
        });
    }
}
