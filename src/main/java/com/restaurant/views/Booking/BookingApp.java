package com.restaurant.views.Booking;

import com.restaurant.controllers.BookingController;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.di.Injector;
import com.restaurant.views.BaseApp;

import javax.swing.*;

public class BookingApp extends BaseApp {
    public BookingApp(BaseApp prevApp) {
        super("Booking Management", prevApp);
        getContentPane().add(new BookingListView());
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            BookingApp app = new BookingApp(null);
            app.setVisible(true);
        });
    }
}