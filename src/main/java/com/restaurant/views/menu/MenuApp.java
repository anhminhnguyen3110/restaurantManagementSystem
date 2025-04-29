package com.restaurant.views.menu;

import com.restaurant.views.BaseApp;

import javax.swing.*;

public class MenuApp extends BaseApp {
    public MenuApp(BaseApp prevApp) {
        super("Menu Management", prevApp);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Menus", new MenuListView());
        getContentPane().add(tabs);
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            MenuApp app = new MenuApp(null);
            app.setVisible(true);
        });
    }
}